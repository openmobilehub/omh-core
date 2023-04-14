package com.omh.android.coreplugin.model

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

open class DefaultService @Inject constructor(project: Project) {
    private val auth: Property<String> = project.objects.property(String::class.java)
    private val maps: Property<String> = project.objects.property(String::class.java)
    private val storage: Property<String> = project.objects.property(String::class.java)

    fun auth(groupIdWithVersion: String) {
        auth.set(groupIdWithVersion)
    }

    fun maps(groupIdWithVersion: String) {
        maps.set(groupIdWithVersion)
    }

    fun storage(groupIdWithVersion: String) {
        storage.set(groupIdWithVersion)
    }

    private fun getAuth(): String = if (
        auth.isPresent && auth.get().trim().isNotEmpty()
    ) auth.get() else ""

    private fun getMaps(): String = if (
        maps.isPresent && maps.get().trim().isNotEmpty()
    ) maps.get() else ""

    private fun getStorage(): String = if (
        storage.isPresent && storage.get().trim().isNotEmpty()
    ) storage.get() else ""

    internal fun dependenciesList(): List<String> {
        val authDependency = getAuth()
        val mapsDependency = getMaps()
        val storageDependency = getStorage()
        val dependencyList = mutableListOf<String>()

        if (authDependency.isNotEmpty()) {
            dependencyList.add(authDependency)
        }
        if (mapsDependency.isNotEmpty()) {
            dependencyList.add(mapsDependency)
        }
        if (storageDependency.isNotEmpty()) {
            dependencyList.add(storageDependency)
        }

        return dependencyList
    }
}
