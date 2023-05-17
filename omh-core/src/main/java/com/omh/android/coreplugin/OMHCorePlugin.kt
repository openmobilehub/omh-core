package com.omh.android.coreplugin

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.omh.android.coreplugin.utils.setupBuildVariantsAccordingToConfig
import com.omh.android.coreplugin.utils.setupExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Represents the plugin itself.
 */
class OMHCorePlugin : Plugin<Project> {

    override fun apply(gradleProject: Project) {
        val omhExtension = gradleProject.setupExtension()
        val androidExtension = gradleProject.extensions.getByType(
            ApplicationAndroidComponentsExtension::class.java
        )

        gradleProject.setupBuildVariantsAccordingToConfig(
            androidExtension,
            omhExtension
        )
    }
}
