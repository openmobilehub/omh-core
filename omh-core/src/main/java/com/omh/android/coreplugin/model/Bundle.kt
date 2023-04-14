package com.omh.android.coreplugin.model

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

    internal fun getDependencies(): List<String> {
        val pathsList = mutableListOf<String>()
        if (auth.isThereGmsService()) {
            pathsList.add(auth.gmsService())
        }
        if (auth.isThereNonGmsService()) {
            pathsList.add(auth.nonGmsService())
        }
        if (maps.isThereGmsService()) {
            pathsList.add(maps.gmsService())
        }
        if (maps.isThereNonGmsService()) {
            pathsList.add(maps.nonGmsService())
        }
        if (storage.isThereGmsService()) {
            pathsList.add(storage.gmsService())
        }
        if (storage.isThereNonGmsService()) {
            pathsList.add(storage.nonGmsService())
        }
        return pathsList
    }
}
