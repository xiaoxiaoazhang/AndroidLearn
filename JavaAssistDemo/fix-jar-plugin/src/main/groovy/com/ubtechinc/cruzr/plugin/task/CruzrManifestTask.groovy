package com.ubtechinc.cruzr.plugin.task

import com.ubtechinc.cruzr.common.utils.IOHelper
import groovy.xml.Namespace
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

public class CruzrManifestTask extends DefaultTask {
    List<String> manifestPaths = []
    public CruzrManifestTask() {
    }

    @TaskAction
    void updateManifest() {
        // Parse the AndroidManifest.xml
        for (String manifestPath : manifestPaths) {
            project.logger.error("AndroidManifest.xml path ${manifestPath}")
            String applicationName = readManifestApplicationName(manifestPath)
            project.logger.error("applicationName is ${applicationName}")
        }
    }

    static String readManifestApplicationName(String manifestPath) {
        def isr = null
        try {
            isr = new InputStreamReader(new FileInputStream(manifestPath), "utf-8")
            def xml = new XmlParser().parse(isr)
            def ns = new Namespace("http://schemas.android.com/apk/res/android", "android")

            def application = xml.application[0]
            if (application) {
                return application.attributes()[ns.name]
            } else {
                return null
            }
        } finally {
            IOHelper.closeQuietly(isr)
        }
    }
}
