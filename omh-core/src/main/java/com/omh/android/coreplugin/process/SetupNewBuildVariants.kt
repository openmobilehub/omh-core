package com.omh.android.coreplugin.process

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.omh.android.coreplugin.model.OMHExtension
import com.omh.android.coreplugin.process.Helper.generateNewBuildTypeName
import com.omh.android.coreplugin.process.Helper.getBundleDependencies
import com.omh.android.coreplugin.process.Helper.getBundleGmsPaths
import com.omh.android.coreplugin.process.Helper.getBundleNonGmsPaths
import com.omh.android.coreplugin.process.Helper.getBundlesNames
import com.omh.android.coreplugin.process.Helper.getUserAppBuildType
import com.omh.android.coreplugin.utils.addDependencyToBuildType
import org.gradle.api.Project

internal object SetupNewBuildVariants {

    private fun joinBundlesAndUserBuildTypesForNewBuildVariants(
        alreadyDefinedBuildTypes: List<String>,
        createdBuildTypesList: MutableList<String>,
        omhExtension: OMHExtension,
        appExtension: ApplicationExtension,
        project: Project
    ) {
        getBundlesNames(omhExtension).forEach { bundleNameForNewBuildVariant ->
            val dependenciesToAdd = getBundleDependencies(
                bundleNameForNewBuildVariant, omhExtension
            )

            val bundleGmsPaths = getBundleGmsPaths(
                bundleNameForNewBuildVariant, omhExtension
            )

            val bundleNonGmsPaths = getBundleNonGmsPaths(
                bundleNameForNewBuildVariant, omhExtension
            )


            alreadyDefinedBuildTypes.forEach { userBuildTypeAlreadyDefined ->
                val finalBuildType = generateNewBuildTypeName(
                    userBuildTypeAlreadyDefined, bundleNameForNewBuildVariant
                )

                createNewBuildVariantWithDependencies(
                    finalBuildType,
                    userBuildTypeAlreadyDefined,
                    dependenciesToAdd,
                    bundleGmsPaths,
                    bundleNonGmsPaths,
                    appExtension,
                    project
                )
                createdBuildTypesList.add(finalBuildType)
            }
        }
    }

    private fun createNewBuildVariantWithDependencies(
        newBuildVariant: String,
        existingBuildType: String,
        dependenciesToAdd: List<String>,
        bundleGmsPaths: List<String>,
        bundleNonGmsPaths: List<String>,
        appExtension: ApplicationExtension,
        project: Project
    ) {
        appExtension.buildTypes.create(newBuildVariant) {
            // Function that lets you copy configurations from an existing build type
            initWith(getUserAppBuildType(existingBuildType, appExtension))
            // then configure only the settings you want to change
            if (dependenciesToAdd.isNotEmpty()) {
                for (bundleDependency in dependenciesToAdd) {
                    project.addDependencyToBuildType(bundleDependency, newBuildVariant)
                }
            }
            // add previously defined build config fields from default section
            for (bundlePath in bundleGmsPaths) {
                buildConfigField("String", "GMS_PATH", "\"$bundlePath\"")
            }

            if (bundleGmsPaths.isEmpty()) {
                buildConfigField("String", "GMS_PATH", "null")
            }

            for (bundlePath in bundleNonGmsPaths) {
                buildConfigField("String", "NON_GMS_PATH", "\"$bundlePath\"")
            }

            if (bundleNonGmsPaths.isEmpty()) {
                buildConfigField("String", "NON_GMS_PATH", "null")
            }
        }
    }

    private fun addDefaultDependenciesToUserBuildTypes(
        alreadyDefinedBuildTypes: List<String>,
        omhExtension: OMHExtension,
        project: Project
    ) {
        omhExtension.getDefaultServices()?.also {
            alreadyDefinedBuildTypes.forEach { userBuildType ->
                for (defaultDependency in it.dependenciesList()) {
                    project.addDependencyToBuildType(defaultDependency, userBuildType)
                }
            }
        }
    }

    fun execute(
        alreadyDefinedBuildTypes: List<String>,
        createdBuildTypesList: MutableList<String>,
        omhExtension: OMHExtension,
        appExtension: ApplicationExtension,
        project: Project
    ) {
        joinBundlesAndUserBuildTypesForNewBuildVariants(
            alreadyDefinedBuildTypes,
            createdBuildTypesList,
            omhExtension,
            appExtension,
            project
        )
        addDefaultDependenciesToUserBuildTypes(
            alreadyDefinedBuildTypes,
            omhExtension,
            project
        )
    }
}
