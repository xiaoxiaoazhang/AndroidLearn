package com.ubtechinc.cruzr.plugin.transform

import com.android.build.api.transform.Format
import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.GradleException
import org.gradle.api.Project

import org.apache.commons.io.FileUtils
import org.apache.commons.codec.digest.DigestUtils

import java.util.regex.Pattern

public class CruzrTestTransform extends Transform {

    Project project

    public CruzrTestTransform(Project project) {
        this.project = project
        project.logger.error("transform called")
    }

    @Override
    String getName() {
        return "CruzrTestTransform"
    }

    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
    //这样确保其他类型的文件不会传入
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transform的作用范围
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    //具体的处理
    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        project.logger.error("transform called2")
        File rootLocation = null
        try {
            rootLocation = outputProvider.rootLocation
        } catch (Throwable e) {
            //android gradle plugin 3.0.0+ 修改了私有变量，将其移动到了IntermediateFolderUtils中去
            rootLocation = outputProvider.folderUtils.getRootFolder()
        }
        if (rootLocation == null) {
            throw new GradleException("can't get transform root location")
        }
        println ">>> rootLocation: ${rootLocation}"
        // Compatible with path separators for window and Linux, and fit split param based on 'Pattern.quote'
        def variantDir = rootLocation.absolutePath.split(getName() + Pattern.quote(File.separator))[1]
        println ">>> variantDir: ${variantDir}"
        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        inputs.each { TransformInput input ->
            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput->
                project.logger.error("directoryInput -> ${directoryInput.file.absolutePath}")
                //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
//                MyInject.injectDir(directoryInput.file.absolutePath,"com\\hc\\hcplugin")
//                MyInject.injectDir(directoryInput.file.absolutePath,"com"+File.separator+"hc"+File.separator+"hcplugin")
                // 获取output目录
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            //对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput->
                project.logger.error("jarInput -> ${jarInput.file.absolutePath}")
                //jar文件一般是第三方依赖库jar文件

                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if(jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0,jarName.length()-4)
                }
                //生成输出路径
                def dest = outputProvider.getContentLocation(jarName+md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }
}
