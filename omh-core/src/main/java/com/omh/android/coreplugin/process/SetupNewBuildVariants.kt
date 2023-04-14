package com.omh.android.coreplugin.process

import com.android.build.api.dsl.ApplicationExtension
import com.omh.android.coreplugin.model.OMHExtension
import com.omh.android.coreplugin.process.Helper.generateNewBuildTypeName
import com.omh.android.coreplugin.process.Helper.getBundleDependencies
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

            alreadyDefinedBuildTypes.forEach { userBuildTypeAlreadyDefined ->
                val finalBuildType = generateNewBuildTypeName(
                    userBuildTypeAlreadyDefined, bundleNameForNewBuildVariant
                )

                createNewBuildVariantWithDependencies(
                    finalBuildType,
                    userBuildTypeAlreadyDefined,
                    dependenciesToAdd,
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
