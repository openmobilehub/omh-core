package com.omh.android.coreplugin.model

import com.omh.android.coreplugin.utils.AUTH_GMS_ADDRESS
import com.omh.android.coreplugin.utils.AUTH_NGMS_ADDRESS
import com.omh.android.coreplugin.utils.ApiPath
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@SuppressWarnings("TooManyFunctions")
open class Bundle @Inject constructor(project: Project) {
    internal val id: Property<String> = project.objects.property(String::class.java)
    private val auth: Service = project.objects.newInstance(
        Service::class.java,
        project,
        Service.AUTH,
        AUTH_GMS_ADDRESS,
        AUTH_NGMS_ADDRESS
    )
    private val maps: Service = project.objects.newInstance(
        Service::class.java,
        project,
        Service.MAPS,
        "",
        ""
    ) // TODO ADD REFLECTION PATHS
    private val storage: Service = project.objects.newInstance(
        Service::class.java,
        project,
        Service.STORAGE,
        "",
        ""
    ) // TODO ADD REFLECTION PATHS

    private val enabledServices: MutableSet<Service> = mutableSetOf()

    private val dependencies = mutableListOf<String>()

    // region Gradle Groovy
    fun auth(configuration: Closure<Service>) {
        enabledServices.add(auth)
        configuration.delegate = auth
        configuration.call()
    }

    fun maps(configuration: Closure<Service>) {
        enabledServices.add(maps)
        configuration.delegate = maps
        configuration.call()
    }

    fun storage(configuration: Closure<Service>) {
        enabledServices.add(storage)
        configuration.delegate = storage
        configuration.call()
    }
    //endregion

    // region Gradle Kotlin
    fun auth(configuration: Action<in Service>) {
        enabledServices.add(auth)
        configuration.execute(auth)
    }

    fun maps(configuration: Action<in Service>) {
        enabledServices.add(maps)
        configuration.execute(maps)
    }

    fun storage(configuration: Action<in Service>) {
        enabledServices.add(storage)
        configuration.execute(storage)
    }
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

    internal fun getGmsPaths(): Map<String, String> {
        return getReflectionPaths(auth::isThereGmsService, auth::getGmsPath)
    }

    internal fun getNonGmsPaths(): Map<String, String> {
        return getReflectionPaths(auth::isThereNonGmsService, auth::getNonGmsPath)
    }

    private fun getReflectionPaths(
        validator: () -> Boolean,
        getter: () -> String
    ): Map<String, String> {
        val nonGmsPathsList = mutableMapOf<String, String>()
        for (service in enabledServices) {
            // NULL will be converted to the primitive type in the BuildConfigField.
            val pathValue = if (validator()) getter() else "null"
            nonGmsPathsList[service.key] = pathValue
        }
        return nonGmsPathsList
    }
}
