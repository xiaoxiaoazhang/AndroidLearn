package com.ubtechinc.cruzr.plugin.transform

import com.android.annotations.NonNull
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformTask
import com.google.common.collect.Lists
import com.ubtechinc.cruzr.common.utils.IOHelper
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.execution.TaskExecutionGraphListener

import java.lang.reflect.Field
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

public class CruzrTransform extends Transform {

    Project project

    File baseDexDir

    File mainDexListFile

    String varName

    String varDirName

    String oldApkPath

    def variant

    def dexTransform

    CruzrTransform(Project project, variant, dexTransform) {
        this.dexTransform = dexTransform
        this.project = project
        this.variant = variant
        this.varName = variant.name.capitalize()
        this.varDirName = variant.getDirName()
        this.oldApkPath = project.cruzrExtension.oldApk
        if (dexTransform.mainDexListFile instanceof File) {
            this.mainDexListFile = dexTransform.mainDexListFile
        } else {
            this.mainDexListFile = dexTransform.mainDexListFile.getSingleFile()
        }
    }

    @NonNull
    @Override
    public Set<QualifiedContent.ContentType> getOutputTypes() {
        return dexTransform.getOutputTypes()
    }

    @NonNull
    @Override
    public Collection<File> getSecondaryFileInputs() {
        return dexTransform.getSecondaryFileInputs()
    }

    @NonNull
    @Override
    public Collection<File> getSecondaryDirectoryOutputs() {
        return dexTransform.getSecondaryDirectoryOutputs()
    }

    @NonNull
    @Override
    public Map<String, Object> getParameterInputs() {
        return dexTransform.getParameterInputs()
    }

    @Override
    String getName() {
        return dexTransform.getName()
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return dexTransform.getInputTypes()
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return dexTransform.getScopes()
    }

    @Override
    boolean isIncremental() {
        return dexTransform.isIncremental()
    }

    public static void traversal(ZipFile zipFile, Closure callback) {
        try {
            Enumeration<? extends ZipEntry> enumeration = zipFile.entries()
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = enumeration.nextElement()
                callback.call(entry, zipFile.getInputStream(entry).bytes)
            }
        } catch (IOException e) {
            e.printStackTrace()
            IOHelper.closeQuietly(zipFile)
        }
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, IOException, InterruptedException {
        // because multi dex is enable,we only process jar file.
        List<JarInput> jarInputs = Lists.newArrayList()
        for (TransformInput input : transformInvocation.getInputs()) {
            jarInputs.addAll(input.getJarInputs())
        }
        for (JarInput jarInput : jarInputs) {
            project.logger.error(jarInput.getName())
        }
        //because the multi-dex is turned on,so the jarInput.size()==1 in theory.
        if (jarInputs.size() != 1) {
            project.logger.error("jar input size is ${jarInputs.size()}, expected is 1. we will skip immutable dex!")
            dexTransform.transform(transformInvocation)
            return
        }
        //get all old dex
        if (oldApkPath == null || oldApkPath.isEmpty()) return
        ArrayList<File> oldDexList = new ArrayList<>()
        traversal(new ZipFile(oldApkPath), { ZipEntry zipEntry, byte[] bytes ->
            if (zipEntry.name.startsWith("classes") && zipEntry.name.endsWith(".dex")) {
                project.logger.info("find dex: ${zipEntry.name} in old apk. ")
                File classDxFile = new File(baseDexDir, zipEntry.name)
                classDxFile.withDataOutputStream { output ->
                    output.write(bytes, 0, bytes.length)
                    output.close()
                }
                oldDexList.add(classDxFile)
            }
        })
    }

    public static void inject(Project project, def variant) {
        project.logger.info("prepare inject dex transform ")
        if (!variant.mergedFlavor.multiDexEnabled) {
            project.logger.warn("multidex is disabled. we will not replace the dex transform.")
            return
        }
        try {
            Class.forName("com.android.build.gradle.internal.transforms.DexTransform")
        } catch (ClassNotFoundException e) {
            return
        }

        project.getGradle().getTaskGraph().addTaskExecutionGraphListener(new TaskExecutionGraphListener() {
            @Override
            public void graphPopulated(TaskExecutionGraph taskGraph) {
                for (Task task : taskGraph.getAllTasks()) {
                    if (task.project != project) {
                        continue
                    }
                    if (task instanceof TransformTask && task.name.toLowerCase().contains(variant.name.toLowerCase())) {
                        if (((TransformTask) task).getTransform().getClass() == Class.forName("com.android.build.gradle.internal.transforms.DexTransform") && !(((TransformTask) task).getTransform() instanceof CruzrTransform)) {
                            project.logger.warn("find dex transform. transform class: " + task.transform.getClass() + " . task name: " + task.name)

                            def dexTransform = task.transform
                            CruzrTransform hookDexTransform = new CruzrTransform(project,
                                    variant, dexTransform)
                            project.logger.info("variant name: " + variant.name)

                            Field field = TransformTask.class.getDeclaredField("transform")
                            field.setAccessible(true)
                            field.set(task, hookDexTransform)
                            project.logger.warn("transform class after hook: " + task.transform.getClass())
                            break
                        }
                    }
                }
            }
        })

    }
}
