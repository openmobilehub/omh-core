package com.omh.android.coreplugin.model

import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.provider.Property

open class ServiceDetail @Inject constructor(
    project: Project,
    private val defaultPath: String,
) {
    private val dependencyProperty: Property<String> = project.objects.property(String::class.java)
    private val pathProperty: Property<String> = project.objects.property(String::class.java)

    internal val isDependencySet: Boolean
        get() {
            val value: String? = dependencyProperty.orNull
            return !value.isNullOrBlank() || !value.isNullOrEmpty()
        }

    var dependency: String?
        set(value) = dependencyProperty.set(value)
        get() = dependencyProperty.orNull
    var path: String
        set(value) = pathProperty.set(value)
        get() = dependencyProperty.orNull ?: defaultPath

    internal fun getDependency(): String = dependencyProperty.get()
    internal fun getPath(): String = pathProperty.getOrElse(defaultPath)
}
