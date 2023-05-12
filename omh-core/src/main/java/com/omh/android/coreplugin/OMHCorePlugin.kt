package com.omh.android.coreplugin

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.omh.android.coreplugin.utils.setupBuildVariantsAccordingToConfig
import com.omh.android.coreplugin.utils.setupExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * OMHCorePlugin supplies and help the developers to configure, enable and set-up the OMH SDK in
 * their projects.
 *
 * This plugin automatically implements the necessary dependencies and enable the custom-build
 * variants to allow you compile the different builds to use the defined providers.
 *
 * Configuration example(Assume as an illustrative example, an auth service):
 *
 * ```kotlin
 * // Using Kotlin DSL.
 * omhConfig {
 *     bundle("worldwide") {
 *         auth {
 *             addGmsService("com.omh.android:auth-api-gms:1.0-SNAPSHOT")
 *             addNonGmsService("com.omh.android:auth-api-non-gms:1.0-SNAPSHOT")
 *         }
 *     }
 *
 *     bundle("gmsStore") {
 *         auth {
 *             addGmsService("com.omh.android:auth-api-gms:1.0-SNAPSHOT")
 *         }
 *     }
 *
 *     bundle("nonGmsStore") {
 *         auth {
 *             addNonGmsService("com.omh.android:auth-api-non-gms:1.0-SNAPSHOT")
 *         }
 *     }
 * }
 * ```
 */
class OMHCorePlugin : Plugin<Project> {

    override fun apply(gradleProject: Project) {
        val omhExtension = gradleProject.setupExtension()
        val androidExtension = gradleProject.extensions.getByType(
            ApplicationAndroidComponentsExtension::class.java
        )

        val createdBuildTypesList = mutableListOf<String>()
        gradleProject.setupBuildVariantsAccordingToConfig(
            androidExtension,
            omhExtension,
            createdBuildTypesList
        )
    }
}
