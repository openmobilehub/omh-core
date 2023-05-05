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
        Service.AUTH,
        AUTH_GMS_ADDRESS,
        AUTH_NGMS_ADDRESS
    )
    private val maps: Service = project.objects.newInstance(
        Service::class.java,
        Service.MAPS,
        "",
        ""
    ) // TODO ADD REFLECTION PATHS
    private val storage: Service = project.objects.newInstance(
        Service::class.java,
        Service.STORAGE,
        "",
        ""
    ) // TODO ADD REFLECTION PATHS

    private val enabledServices: MutableSet<Service> = mutableSetOf()

    private val dependencies = mutableListOf<String>()

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

    fun getReflectionPaths(): Map<String, String> {
        val pathMap = mutableMapOf<String, String>()
        for (service in enabledServices) {
            addPathsFromService(pathMap, service)
        }
        return pathMap
    }

    private fun addPathsFromService(
        pathMap: MutableMap<String, String>,
        service: Service
    ) {
        pathMap["${service.key}_GMS_PATH"] = selectPathValue(
            validator = service::isThereGmsService,
            getter = service::getGmsPath
        )
        pathMap["${service.key}_NON_GMS_PATH"] = selectPathValue(
            validator = service::isThereNonGmsService,
            getter = service::getNonGmsPath
        )
    }

    private fun selectPathValue(validator: () -> Boolean, getter: () -> String): String {
        // NULL will be converted to the primitive type in the BuildConfigField.
        return if (validator()) getter() else "null"
    }
}
