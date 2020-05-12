package com.ubtechinc.cruzr.plugin.util

import javassist.ClassPool
import javassist.CtClass
import javassist.CtConstructor
import org.gradle.api.Project;

public class Injecter {

    public static class MyCustomInjecter extends BaseInjector {

        @Override
        def injectClass(ClassPool pool, String path, Map config) {
            File dir = new File(path)
            if (dir.isDirectory()) {
                dir.eachFileRecurse { File file ->
                    String filePath = file.absolutePath
                    //确保当前文件是class文件，并且不是系统自动生成的class文件
                    if (filePath.endsWith(".class")
                            && !filePath.contains('R$')
                            && !filePath.contains('R.class')
                            && !filePath.contains("BuildConfig.class")) {
                        def packageName = config.get("packageName")
                        int index = filePath.indexOf(packageName);
                        if (index != -1) {
                            String injectStr = config.get("injectStr")
                            println("injectStr >>> " + injectStr)
                            int end = filePath.length() - 6 // .class = 6
                            String className = filePath.substring(index, end)
                                    .replace('\\', '.').replace('/', '.')
                            //开始修改class文件
                            println("className >>> " + className)
                            CtClass c = pool.getCtClass(className)
                            if (c.isFrozen()) {
                                c.defrost()
                            }

                            CtConstructor[] cts = c.getDeclaredConstructors()
                            if (cts == null || cts.length == 0) {
                                //手动创建一个构造函数
                                CtConstructor constructor = new CtConstructor(new CtClass[0], c)
                                constructor.insertBeforeBody(injectStr)
                                c.addConstructor(constructor)
                            } else {
                                cts[0].insertBeforeBody(injectStr)
                            }
                            c.writeFile(path)
//                            c.writeFile(CommonData.getClassPath(c.name))
                            c.detach()
                        }
                    }
                }
            }
        }
    }
}
