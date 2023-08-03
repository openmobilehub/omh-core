import org.gradle.api.Project

fun Project.getPropertyOrFail(propertyKey: String) =
    properties.getOrDefault(propertyKey, null)?.toString()
        ?: throw IllegalStateException("You must define $propertyKey in your gradle.properties")