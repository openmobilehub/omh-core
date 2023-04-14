package com.omh.android.coreplugin.model

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

open class Service @Inject constructor(project: Project) {
    private val gms: Property<String> = project.objects.property(String::class.java)
    private val nonGms: Property<String> = project.objects.property(String::class.java)

    internal fun isThereGmsService() = gms.isPresent && gms.get().trim().isNotEmpty()
    internal fun isThereNonGmsService() = nonGms.isPresent && nonGms.get().trim().isNotEmpty()

    fun addGmsService(groupIdWithVersion: String) {
        gms.set(groupIdWithVersion)
    }

    fun addNonGmsService(groupIdWithVersion: String) {
        nonGms.set(groupIdWithVersion)
    }

    internal fun gmsService() = gms.get()
    internal fun nonGmsService() = nonGms.get()
}
