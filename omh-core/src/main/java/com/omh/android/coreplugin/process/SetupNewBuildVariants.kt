package com.omh.android.coreplugin.process

import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.ApplicationExtension
import com.omh.android.coreplugin.model.OMHExtension
import com.omh.android.coreplugin.process.Helper.generateNewBuildTypeName
import com.omh.android.coreplugin.process.Helper.getBundleDependencies
import com.omh.android.coreplugin.process.Helper.getBundleReflectionPaths
import com.omh.android.coreplugin.process.Helper.getBundlesNames
import com.omh.android.coreplugin.process.Helper.getUserAppBuildType
import com.omh.android.coreplugin.utils.BundleData
import com.omh.android.coreplugin.utils.addDependencyToBuildType
import org.gradle.api.Project

internal object SetupNewBuildVariants {

    private fun Project.joinBundlesAndUserBuildTypesForNewBuildVariants(
        predefinedBuildTypes: List<String>,
        createdBuildTypes: MutableList<String>,
        omhExtension: OMHExtension,
        appExtension: ApplicationExtension
    ) {
        getBundlesNames(omhExtension).forEach { bundleName ->
            handleBundle(
                bundleName,
                omhExtension,
                predefinedBuildTypes,
                appExtension,
                createdBuildTypes
            )
        }
    }

    private fun Project.handleBundle(
        bundleName: String,
        omhExtension: OMHExtension,
        predefinedBuildTypes: List<String>,
        appExtension: ApplicationExtension,
        createdBuildTypes: MutableList<String>
    ) {
        val dependenciesToAdd: List<String> = getBundleDependencies(
            bundleName = bundleName,
            omhExt = omhExtension
        )
        val reflectionPaths: Map<String, String> = getBundleReflectionPaths(
            bundleName = bundleName,
            omhExt = omhExtension
        )
        val bundleData = BundleData(bundleName, dependenciesToAdd, reflectionPaths)

        predefinedBuildTypes.forEach { predefinedBuildType ->
            handleNewBuildType(
                predefinedBuildType,
                bundleData,
                appExtension,
                createdBuildTypes
            )
        }
    }

    private fun Project.handleNewBuildType(
        predefinedBuildTypeName: String,
        bundleData: BundleData,
        appExtension: ApplicationExtension,
        createdBuildTypesList: MutableList<String>
    ) {
        val finalBuildType: String = generateNewBuildTypeName(
            alreadyDefinedBuildType = predefinedBuildTypeName,
            bundleName = bundleData.name
        )

        appExtension.buildTypes.create(finalBuildType) {
            // Function that lets you copy configurations from an existing build type
            val userAppBuildType: ApplicationBuildType = getUserAppBuildType(
                alreadyDefinedBuildType = predefinedBuildTypeName,
                appExt = appExtension
            )
            initWith(userAppBuildType)
            // then configure only the settings you want to change
            addDependencies(bundleData.dependencies, finalBuildType)
            // Add the reflection path to the BuildConfigField
            addReflectionPaths(bundleData)
        }
        createdBuildTypesList.add(finalBuildType)
    }

    private fun ApplicationBuildType.addReflectionPaths(bundleData: BundleData) {
        for ((variableName: String, bundlePath: String) in bundleData.reflectionPaths) {
            buildConfigField("String", variableName, "\"$bundlePath\"")
        }
    }

    private fun Project.addDependencies(
        dependenciesToAdd: List<String>,
        newBuildVariant: String
    ) {
        if (dependenciesToAdd.isEmpty()) return
        for (bundleDependency in dependenciesToAdd) {
            addDependencyToBuildType(bundleDependency, newBuildVariant)
        }
    }

    fun execute(
        predefinedBuildTypes: List<String>,
        createdBuildTypesList: MutableList<String>,
        omhExtension: OMHExtension,
        appExtension: ApplicationExtension,
        project: Project
    ) {
        project.joinBundlesAndUserBuildTypesForNewBuildVariants(
            predefinedBuildTypes,
            createdBuildTypesList,
            omhExtension,
            appExtension
        )
    }
}
