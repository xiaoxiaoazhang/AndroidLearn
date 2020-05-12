package com.ubtechinc.cruzr.plugin.util

import javassist.ClassPool
import org.gradle.api.Project;

public interface IClassInjector {

    /**
     * 设置project对象
     * @param project
     */
    void setProject(Project project)

    /**
     * 设置variant目录关键串
     * @param variantDir
     */
    void setVariantDir(String variantDir)
    /**
     * 注入器名称
     */
    def name()

    /**
     * 对 dir 目录中的 Class 进行注入
     */
    def injectClass(ClassPool pool, String dir, Map config)
}
