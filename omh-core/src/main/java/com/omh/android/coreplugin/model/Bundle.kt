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

    private val pathsList = mutableListOf<String>()

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
        if (pathsList.contains(apiDependency)) return
        pathsList.add(apiDependency)
    }

    internal fun getDependencies(): List<String> {
        pathsList.clear()
        if (auth.isThereGmsService()) {
            pathsList.add(auth.gmsService())
            addApiDependencyIfNoExists(ApiPath.AUTH)
        }
        if (auth.isThereNonGmsService()) {
            pathsList.add(auth.nonGmsService())
            addApiDependencyIfNoExists(ApiPath.AUTH)
        }
        if (maps.isThereGmsService()) {
            pathsList.add(maps.gmsService())
            addApiDependencyIfNoExists(ApiPath.MAPS)
        }
        if (maps.isThereNonGmsService()) {
            pathsList.add(maps.nonGmsService())
            addApiDependencyIfNoExists(ApiPath.MAPS)
        }
        if (storage.isThereGmsService()) {
            pathsList.add(storage.gmsService())
            addApiDependencyIfNoExists(ApiPath.STORAGE)
        }
        if (storage.isThereNonGmsService()) {
            pathsList.add(storage.nonGmsService())
            addApiDependencyIfNoExists(ApiPath.STORAGE)
        }
        return pathsList
    }
}
