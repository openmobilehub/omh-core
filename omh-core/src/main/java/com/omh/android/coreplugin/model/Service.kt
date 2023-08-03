package com.omh.android.coreplugin.model

import org.gradle.api.Project
import javax.inject.Inject
import org.gradle.api.Action

/**
 * Represents the type of dependency to be added. It can only be GMS o NON GMS
 */
open class Service @Inject constructor(
    project: Project,
    val key: String,
    gmsPath: String,
    nonGmsPath: String
) {

    private val gmsServiceDetail: ServiceDetail = project.objects.newInstance(
        ServiceDetail::class.java,
        gmsPath,
    )
    private val ngmsServiceDetail: ServiceDetail = project.objects.newInstance(
        ServiceDetail::class.java,
        nonGmsPath,
    )

    internal val isGmsDependencySet: Boolean
        get() = gmsServiceDetail.isDependencySet
    internal val isNonGmsDependencySet: Boolean
        get() = ngmsServiceDetail.isDependencySet

    fun gmsService(configuration: Action<in ServiceDetail>) {
        configuration.execute(gmsServiceDetail)
    }

    fun nonGmsService(configuration: Action<in ServiceDetail>) {
        configuration.execute(ngmsServiceDetail)
    }

    internal val gmsService
        get() = gmsServiceDetail.getDependency()
    internal val nonGmsService
        get() = ngmsServiceDetail.getDependency()
    internal val gmsPath
        get() = gmsServiceDetail.getPath()
    internal val nonGmsPath
        get() = ngmsServiceDetail.getPath()

    companion object {
        internal const val AUTH = "AUTH"
        internal const val STORAGE = "STORAGE"
        internal const val MAPS = "MAPS"
    }

}
