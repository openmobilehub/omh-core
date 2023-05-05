package com.omh.android.coreplugin.model

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject
import org.gradle.api.Action

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

    internal fun isThereGmsService(): Boolean = gmsServiceDetail.isDependencySet

    internal fun isThereNonGmsService(): Boolean = ngmsServiceDetail.isDependencySet

    fun gmsService(configuration: Action<in ServiceDetail>) {
        configuration.execute(gmsServiceDetail)
    }

    fun nonGmsService(configuration: Action<in ServiceDetail>) {
        configuration.execute(ngmsServiceDetail)
    }

    internal fun gmsService() = gmsServiceDetail.getDependency()
    internal fun nonGmsService() = ngmsServiceDetail.getDependency()
    internal fun getGmsPath() = gmsServiceDetail.getPath()
    internal fun getNonGmsPath() = ngmsServiceDetail.getPath()

    companion object {
        internal const val AUTH = "AUTH"
        internal const val STORAGE = "STORAGE"
        internal const val MAPS = "MAPS"
    }

}
