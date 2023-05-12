package com.omh.android.coreplugin.model

import com.omh.android.coreplugin.utils.ERROR_BUNDLES_BUT_NO_SERVICES
import com.omh.android.coreplugin.utils.ERROR_BUNDLES_WITHOUT_NAME
import com.omh.android.coreplugin.utils.ERROR_BUNDLES_WITH_INCORRECT_NAME
import com.omh.android.coreplugin.utils.ERROR_DEFAULTSERVICES_ARE_NOT_IN_BUNDLES
import com.omh.android.coreplugin.utils.ERROR_DEFAULTSERVICES_AT_LEAST_ONE
import com.omh.android.coreplugin.utils.ERROR_DEFAULTSERVICES_BUT_NO_BUNDLES
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import javax.inject.Inject
import org.gradle.api.Action

open class OMHExtension @Inject constructor(private val project: Project) {

    private val defaultServices: Property<DefaultService> = project.objects.property(
        DefaultService::class.java
    )

    private val bundles: MapProperty<String, Bundle> = project.objects.mapProperty(
        String::class.java,
        Bundle::class.java
    )

    fun bundle(id: String, configuration: Action<in Bundle>) {
        val bundle = project.objects.newInstance(Bundle::class.java, project)
        bundle.id.set(id.trim())
        configuration.execute(bundle)
        bundles.put(bundle.id.get(), bundle)
    }

    fun defaultServices(configuration: DefaultService.() -> Unit) {
        val default = project.objects.newInstance(DefaultService::class.java, project)
        default.apply(configuration)
        defaultServices.set(default)
    }

    //region Validations
    private fun areDefaultServicesNotDefinedInBundles(): Boolean {
        if (defaultServices.isPresent.not()) {
            return false
        }
        val defaultServicesList = defaultServices.get().dependenciesList()
        val bundlesDependenciesList = getDependenciesFromAllBundles()
        return defaultServicesList.all { !bundlesDependenciesList.contains(it) }
    }

    private fun getDependenciesFromAllBundles(): List<String> {
        val dependenciesInAllBundlesList = mutableListOf<String>()
        if (bundles.isPresent && bundles.get().isNotEmpty()) {
            bundles.get().values.forEach {
                dependenciesInAllBundlesList.addAll(it.getDependencies())
            }
        }
        return dependenciesInAllBundlesList
    }

    private fun isThereANamelessBundle(bundlesMap: Map<String, Bundle>): Boolean {
        bundlesMap.forEach { bundle ->
            if (bundle.key.isEmpty()) return true
        }
        return false
    }

    private fun isThereABundleWithIncorrectName(bundlesMap: Map<String, Bundle>): Boolean {
        bundlesMap.forEach { bundle ->
            if (!bundleNameRegex.matches(bundle.key)) return true
        }
        return false
    }

    /**
     * Validate that bundles and services are required at the time of using the plugin in clients.
     */
    @SuppressWarnings("CyclomaticComplexMethod")
    fun validateIntegrity() {
        var errorMsg = ""

        // Default Services Integrity
        if (defaultServices.isPresent) {
            if (defaultServices.get().dependenciesList().isEmpty()) {
                errorMsg = ERROR_DEFAULTSERVICES_AT_LEAST_ONE
            }
            if (errorMsg.isEmpty() && bundles.get().isEmpty()) {
                errorMsg = ERROR_DEFAULTSERVICES_BUT_NO_BUNDLES
            }
            if (errorMsg.isEmpty() && areDefaultServicesNotDefinedInBundles()) {
                errorMsg = ERROR_DEFAULTSERVICES_ARE_NOT_IN_BUNDLES
            }
        }
        // Bundles Integrity
        if (errorMsg.isEmpty() && bundles.isPresent && bundles.get().isNotEmpty()) {
            val bundlesMap = bundles.get()
            if (isThereANamelessBundle(bundlesMap)) {
                errorMsg = ERROR_BUNDLES_WITHOUT_NAME
            }
            if (errorMsg.isEmpty() && isThereABundleWithIncorrectName(bundlesMap)) {
                errorMsg = ERROR_BUNDLES_WITH_INCORRECT_NAME
            }
            if (errorMsg.isEmpty() && getDependenciesFromAllBundles().isEmpty()) {
                errorMsg = ERROR_BUNDLES_BUT_NO_SERVICES
            }
        }

        if (errorMsg.isNotEmpty()) throw InvalidUserDataException(errorMsg)
    }
    //endregion

    internal fun getBundles() = bundles
    internal fun getDefaultServices(): DefaultService? =
        if (defaultServices.isPresent) defaultServices.get() else null

    companion object {
        private val bundleNameRegex = Regex("^[a-zA-Z][a-zA-Z0-9_]*(?:_[a-zA-Z0-9_]+)*$")
    }
}
