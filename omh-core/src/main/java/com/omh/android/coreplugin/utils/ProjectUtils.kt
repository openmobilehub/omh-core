package com.omh.android.coreplugin.utils

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.omh.android.coreplugin.model.OMHExtension
import com.omh.android.coreplugin.process.Helper.getUserBuildTypesNames
import com.omh.android.coreplugin.process.SetupNewBuildVariants.execute
import org.gradle.api.Project

private const val OMH_PLUGIN_EXTENSION = "omhConfig"

internal fun Project.setupExtension(): OMHExtension = extensions.create(
    OMH_PLUGIN_EXTENSION,
    OMHExtension::class.java
)

/**
 * Shortcut method to add a build type implementation in dependencies section.
 */
internal fun Project.addDependencyToBuildType(
    dependency: String,
    buildType: String
) {
    dependencies.add("${buildType}Implementation", dependency)
}

/**
 * Validate require information from gradle and create the build variants
 * according to the implemented OMH services.
 */
internal fun Project.setupBuildVariantsAccordingToConfig(
    androidExtension: ApplicationAndroidComponentsExtension,
    extension: OMHExtension,
    createdBuildTypesList: MutableList<String>
) {
    androidExtension.finalizeDsl { applicationExtension ->
        extension.validateIntegrity()
        val predefinedBuildTypes: List<String> = getUserBuildTypesNames(applicationExtension)
        execute(
            predefinedBuildTypes,
            createdBuildTypesList,
            extension,
            applicationExtension
        )
    }
    androidExtension.beforeVariants { variantBuilder ->
        val name = variantBuilder.name
        if (!createdBuildTypesList.contains(name)) variantBuilder.enable = false
    }
}
