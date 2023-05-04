package com.omh.android.coreplugin.model

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

open class Service @Inject constructor(project: Project) {

    private val gmsProperty: Property<String> = project.objects.property(String::class.java)
    private val gmsPathProperty: Property<String> = project.objects.property(String::class.java)

    private val nonGmsProperty: Property<String> = project.objects.property(String::class.java)
    private val nonGMSPathProperty: Property<String> = project.objects.property(String::class.java)

    internal fun isThereGmsService(): Boolean {
        return gmsProperty.isPresent && gmsProperty.get().trim().isNotEmpty()
    }

    internal fun isThereNonGmsService(): Boolean {
        return nonGmsProperty.isPresent && nonGmsProperty.get().trim().isNotEmpty()
    }

    fun setGmsService(groupIdWithVersion: String, reflectionPath: String) {
        gmsProperty.set(groupIdWithVersion)
        gmsPathProperty.set(reflectionPath)
    }

    fun setNonGmsService(groupIdWithVersion: String, reflectionPath: String) {
        nonGmsProperty.set(groupIdWithVersion)
        nonGMSPathProperty.set(reflectionPath)
    }

    internal fun gmsService() = gmsProperty.get()
    internal fun nonGmsService() = nonGmsProperty.get()
    internal fun getGmsPath() = gmsPathProperty.get()
    internal fun getNonGmsPath() = nonGMSPathProperty.get()

    companion object {
        const val AUTH = "AUTH"
        const val STORAGE = "STORAGE"
        const val MAPS = "MAPS"
    }

}
