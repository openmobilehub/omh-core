plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    id("io.gitlab.arturbosch.detekt")
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
