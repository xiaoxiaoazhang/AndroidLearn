package com.ubtechinc.cruzr.plugin.extension

import org.gradle.api.GradleException

public class CruzrExtension {
    String info
    String oldApk

    CruzrExtension() {
        this.info = "cruzr default info"
        oldApk = ""
    }

    void checkParameter() {
        if (oldApk == null) {
            throw new GradleException("old apk is null, you must set the correct old apk value!")
        }
        File apk = new File(oldApk)
        if (!apk.exists()) {
            throw new GradleException("old apk ${oldApk} is not exist, you must set the correct old apk value!")
        } else if (!apk.isFile()) {
            throw new GradleException("old apk ${oldApk} is a directory, you must set the correct old apk value!")
        }

    }

    @Override
    public String toString() {
        """| oldApk = ${oldApk}
           | info = ${info}
        """.stripMargin()
    }
}