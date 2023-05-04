package com.omh.android.coreplugin.model

import com.omh.android.coreplugin.utils.ApiPath
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

open class Bundle @Inject constructor(project: Project) {
    internal val id: Property<String> = project.objects.property(String::class.java)
    private val auth: Service = project.objects.newInstance(Service::class.java, project)
    private val maps: Service = project.objects.newInstance(Service::class.java, project)
    private val storage: Service = project.objects.newInstance(Service::class.java, project)

    private val dependencies = mutableListOf<String>()

    // region Gradle Groovy
    fun auth(configuration: Closure<Service>) {
        configuration.delegate = auth
        configuration.call()
    }

    fun maps(configuration: Closure<Service>) {
        configuration.delegate = maps
        configuration.call()
    }

    fun storage(configuration: Closure<Service>) {
        configuration.delegate = storage
        configuration.call()
    }
    //endregion

    // region Gradle Kotlin
    fun auth(configuration: Action<in Service>) = configuration.execute(auth)
    fun maps(configuration: Action<in Service>) = configuration.execute(maps)
    fun storage(configuration: Action<in Service>) = configuration.execute(storage)
    //endregion

    private fun addApiDependencyIfNoExists(apiDependency: String) {
        if (dependencies.contains(apiDependency)) return
        dependencies.add(apiDependency)
    }

    internal fun getDependencies(): List<String> {
        dependencies.clear()
        if (auth.isThereGmsService()) {
            dependencies.add(auth.gmsService())
            addApiDependencyIfNoExists(ApiPath.AUTH)
        }
        if (auth.isThereNonGmsService()) {
            dependencies.add(auth.nonGmsService())
            addApiDependencyIfNoExists(ApiPath.AUTH)
        }
        if (maps.isThereGmsService()) {
            dependencies.add(maps.gmsService())
            addApiDependencyIfNoExists(ApiPath.MAPS)
        }
        if (maps.isThereNonGmsService()) {
            dependencies.add(maps.nonGmsService())
            addApiDependencyIfNoExists(ApiPath.MAPS)
        }
        if (storage.isThereGmsService()) {
            dependencies.add(storage.gmsService())
            addApiDependencyIfNoExists(ApiPath.STORAGE)
        }
        if (storage.isThereNonGmsService()) {
            dependencies.add(storage.nonGmsService())
            addApiDependencyIfNoExists(ApiPath.STORAGE)
        }
        return dependencies
    }

    internal fun getGmsPaths(): List<String> {
        val gmsPathsList = mutableListOf<String>()
        if (auth.isThereGmsService()) {
            gmsPathsList.add(auth.getGmsPath())
        }
        return gmsPathsList
    }

    internal fun getNonGmsPaths(): List<String> {
        val nonGmsPathsList = mutableListOf<String>()
        if (auth.isThereNonGmsService()) {
            nonGmsPathsList.add(auth.getNonGmsPath())
        }
        return nonGmsPathsList
    }
}
