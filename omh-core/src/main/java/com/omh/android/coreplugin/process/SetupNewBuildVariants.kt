package com.omh.android.coreplugin.process

import com.android.build.api.dsl.ApplicationBuildType
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
            val dependenciesToAdd: List<String> = getBundleDependencies(
                bundleName = bundleNameForNewBuildVariant,
                omhExt = omhExtension
            )

            val bundleGmsPaths: Map<String, String> = getBundleGmsPaths(
                bundleName = bundleNameForNewBuildVariant,
                omhExt = omhExtension
            )

            val bundleNonGmsPaths: Map<String, String> = getBundleNonGmsPaths(
                bundleName = bundleNameForNewBuildVariant,
                omhExt = omhExtension
            )

            alreadyDefinedBuildTypes.forEach { userBuildTypeAlreadyDefined ->
                handleNewBuildType(
                    userBuildTypeAlreadyDefined,
                    bundleNameForNewBuildVariant,
                    dependenciesToAdd,
                    bundleGmsPaths,
                    bundleNonGmsPaths,
                    appExtension,
                    project,
                    createdBuildTypesList
                )
            }
        }
    }

    private fun handleNewBuildType(
        userBuildTypeAlreadyDefined: String,
        bundleNameForNewBuildVariant: String,
        dependenciesToAdd: List<String>,
        bundleGmsPaths: Map<String, String>,
        bundleNonGmsPaths: Map<String, String>,
        appExtension: ApplicationExtension,
        project: Project,
        createdBuildTypesList: MutableList<String>
    ) {
        val finalBuildType: String = generateNewBuildTypeName(
            alreadyDefinedBuildType = userBuildTypeAlreadyDefined,
            bundleName = bundleNameForNewBuildVariant
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

    private fun createNewBuildVariantWithDependencies(
        newBuildVariant: String,
        existingBuildType: String,
        dependenciesToAdd: List<String>,
        bundleGmsPaths: Map<String, String>,
        bundleNonGmsPaths: Map<String, String>,
        appExtension: ApplicationExtension,
        project: Project
    ) {
        appExtension.buildTypes.create(newBuildVariant) {
            // Function that lets you copy configurations from an existing build type
            initWith(getUserAppBuildType(existingBuildType, appExtension))
            // then configure only the settings you want to change
            addDependencies(dependenciesToAdd, project, newBuildVariant)
            addBuildConfigFields(bundleGmsPaths, bundleNonGmsPaths)
        }
    }

    private fun addDependencies(
        dependenciesToAdd: List<String>,
        project: Project,
        newBuildVariant: String
    ) {
        if (dependenciesToAdd.isEmpty()) return
        for (bundleDependency in dependenciesToAdd) {
            project.addDependencyToBuildType(bundleDependency, newBuildVariant)
        }
    }

    private fun ApplicationBuildType.addBuildConfigFields(
        bundleGmsPaths: Map<String, String>,
        bundleNonGmsPaths: Map<String, String>
    ) {
        // add previously defined build config fields from default section
        for ((moduleName: String, bundlePath: String) in bundleGmsPaths) {
            buildConfigField("String", "${moduleName}_GMS_PATH", "\"$bundlePath\"")
        }

        for ((moduleName: String, bundlePath: String) in bundleNonGmsPaths) {
            buildConfigField("String", "${moduleName}_NON_GMS_PATH", "\"$bundlePath\"")
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
