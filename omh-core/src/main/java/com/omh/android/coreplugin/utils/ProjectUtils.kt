package com.omh.android.coreplugin.utils

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.omh.android.coreplugin.model.OMHExtension
import com.omh.android.coreplugin.process.Helper.getUserBuildTypesNames
import com.omh.android.coreplugin.process.SetupNewBuildVariants
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
 * Analyze and create the build variants according to the implemented OMH services.
 */
internal fun Project.setupBuildVariantsAccordingToConfig(
    androidExtension: ApplicationAndroidComponentsExtension,
    extension: OMHExtension,
    createdBuildTypesList: MutableList<String>
) {
    androidExtension.finalizeDsl {
        extension.validateIntegrity()
        SetupNewBuildVariants.execute(
            getUserBuildTypesNames(it),
            createdBuildTypesList,
            extension,
            it,
            this
        )
    }
}

/**
 * Remove test tasks from the new generated build types.
 */
internal fun ApplicationAndroidComponentsExtension.removeTestsTasksFromGeneratedTypes(
    generatedTypesList: List<String>
) {
    beforeVariants { variantBuilder ->
        if (generatedTypesList.any { variantBuilder.name.contains(it) }) {
            variantBuilder.enableUnitTest = false
            variantBuilder.enableAndroidTest = false
        }
    }
}
