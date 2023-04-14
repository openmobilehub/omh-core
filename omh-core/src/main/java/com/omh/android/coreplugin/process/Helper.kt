package com.omh.android.coreplugin.process

import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.ApplicationExtension
import com.omh.android.coreplugin.model.OMHExtension
import org.gradle.configurationcache.extensions.capitalized

internal object Helper {

    fun getUserBuildTypesNames(
        appExt: ApplicationExtension
    ): List<String> = appExt
        .buildTypes
        .map { buildType -> buildType.name }

    fun getBundlesNames(
        omhExt: OMHExtension
    ): List<String> = omhExt
        .getBundles()
        .get()
        .keys
        .map { bundleId -> bundleId }

    fun getBundleDependencies(
        bundleName: String,
        omhExt: OMHExtension
    ): List<String> = omhExt
        .getBundles()
        .get()[bundleName]?.getDependencies() ?: mutableListOf()

    fun generateNewBuildTypeName(
        alreadyDefinedBuildType: String,
        bundleName: String
    ): String = alreadyDefinedBuildType + bundleName.capitalized()

    fun getUserAppBuildType(
        alreadyDefinedBuildType: String,
        appExt: ApplicationExtension
    ): ApplicationBuildType = appExt.buildTypes.getByName(alreadyDefinedBuildType)

}
