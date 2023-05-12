plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka") version "1.8.10"
}

configure<GradlePluginDevelopmentExtension> {
    plugins {
        create("android-coverage") {
            group = getPropertyOrFail("group")
            id = getPropertyOrFail("id")
            description = getPropertyOrFail("pluginDescription")
            implementationClass = "com.omh.android.coreplugin.OMHCorePlugin"
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(BuildPlugins.android)
    implementation(BuildPlugins.kotlin)
}
