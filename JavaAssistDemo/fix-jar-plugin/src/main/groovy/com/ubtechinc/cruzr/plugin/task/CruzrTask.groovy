package com.ubtechinc.cruzr.plugin.task

import com.ubtechinc.cruzr.plugin.extension.CruzrExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

public class CruzrTask extends DefaultTask {

    CruzrExtension configuration
    def android

    public CruzrTask() {
        outputs.upToDateWhen { false }
        configuration = project.cruzrExtension
        android = project.extensions.android
    }


    @TaskAction
    def tinkerPatch() {
        configuration.checkParameter()
    }
}
