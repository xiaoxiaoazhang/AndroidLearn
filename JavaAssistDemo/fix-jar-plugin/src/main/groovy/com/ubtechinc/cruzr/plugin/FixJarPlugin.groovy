package com.ubtechinc.cruzr.plugin

import com.ubtechinc.cruzr.plugin.extension.CruzrExtension
import com.ubtechinc.cruzr.plugin.task.CruzrManifestTask
import com.ubtechinc.cruzr.plugin.task.CruzrTask
import com.ubtechinc.cruzr.plugin.transform.CruzrTestTransform
import com.ubtechinc.cruzr.plugin.transform.CruzrTransform
import com.ubtechinc.cruzr.plugin.util.FileOperation
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin

class FixJarPlugin implements Plugin<Project> {

    private Project mProject = null

    @Override
    void apply(Project project) {
        mProject = project;
        //osdetector change its plugin name in 1.4.0
        try {
            mProject.apply plugin: 'osdetector'
        } catch (Throwable e) {
            mProject.apply plugin: 'com.google.osdetector'
        }
        ////// 创建参数扩展
        mProject.extensions.create('cruzrExtension', CruzrExtension)
        ////// 结束创建参数扩展

        if (!mProject.plugins.hasPlugin('com.android.application')) {
            throw new GradleException('generateCruzrApk: Android Application plugin required')
        }

        def android = mProject.extensions.android
        mProject.logger.error("compileSdkVersion -> ${android.compileSdkVersion}")
        mProject.logger.error("buildToolsVersion -> ${android.buildToolsVersion}")

        mProject.afterEvaluate {
            def configuration = mProject.cruzrExtension

            mProject.logger.error(configuration.info)

            mProject.logger.error("---------------------- ${mProject.getName()} build start------------------------------------")

            android.applicationVariants.all { variant ->
                def variantName = variant.name.capitalize()

                def instantRunTask = getInstantRunTask(variantName)
                if (instantRunTask != null) {
                    throw new GradleException(
                            "Cruzr does not support instant run mode, please trigger build"
                                    + " by assemble${variantName} or disable instant run"
                                    + " in 'File->Settings...'."
                    )
                }

                for (Task task : mProject.tasks) {
                    mProject.logger.error("task -> ${task.name}")
                }

                CruzrTask cruzrTask = mProject.tasks.create("cruzrTask${variantName}", CruzrTask)

                def agpProcessManifestTask = project.tasks.findByName("process${variantName}Manifest")
                String manifestOutputBaseDir
                try {
                    manifestOutputBaseDir = agpProcessManifestTask.manifestOutputDirectory.asFile.get()
                } catch (Throwable ignored) {
                    manifestOutputBaseDir = agpProcessManifestTask.manifestOutputDirectory
                }

                Set<String> manifestPaths = []
                variant.outputs.each { variantOutput ->
                    if (variant.metaClass.hasProperty(variant, 'assembleProvider')) {
                        cruzrTask.dependsOn variant.assembleProvider.get()
                    } else {
                        cruzrTask.dependsOn variant.assemble
                    }

                    def manifestDir = new File(manifestOutputBaseDir, variantOutput.apkData.dirName)
                    manifestPaths.add(new File(manifestDir, 'AndroidManifest.xml'))
                }

                CruzrManifestTask cruzrManifestTask = mProject.tasks.create("cruzrProcess${variantName}Manifest", CruzrManifestTask)
                cruzrManifestTask.manifestPaths.addAll(manifestPaths)
                cruzrManifestTask.mustRunAfter agpProcessManifestTask

                def agpProcessResourcesTask = project.tasks.findByName("process${variantName}Resources")
                agpProcessResourcesTask.dependsOn cruzrManifestTask

                if (FileOperation.isLegalFile(mProject.cruzrExtension.oldApk)) {
                    CruzrTransform.inject(mProject, variant)
                }
            }

            System.out.println("===================================")
            System.out.println("this is second gradle plugin")
            System.out.println("===================================")
            def isApp = project.plugins.hasPlugin(AppPlugin)
            def isLib = project.plugins.hasPlugin(LibraryPlugin)
            // 仅处理application合包
            if (isApp) {
//                def android2 = project.extensions.getByType(AppExtension)
                def android2 = project.extensions.findByType(AppExtension)
                android2.registerTransform(new CruzrTestTransform(project))

//                android.applicationVariants.all { variant ->
//                    def variantData = variant.variantData
//                    def scope = variantData.scope
//                    def assembleTask = variant.getAssemble()
//                    def installPluginTask = project.task(installPluginTaskName)
//                    installPluginTask.doLast {
//                        pluginDebugger.startHostApp()
//                        pluginDebugger.uninstall()
//                        pluginDebugger.forceStopHostApp()
//                        pluginDebugger.startHostApp()
//                        pluginDebugger.install()
//                    }
//                    installPluginTask.group = AppConstant.TASKS_GROUP
//                }
            }
        }
    }

    Task getInstantRunTask(String variantName) {
        String instantRunTask = "transformClassesWithInstantRunFor${variantName}"
        return mProject.tasks.findByName(instantRunTask)
    }

}
