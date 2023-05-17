plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka") version "1.8.10"
    id("signing")
}

gradlePlugin {
    plugins {
        create("pluginRelease") {
            id = getPropertyOrFail("id")
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

// Publishing block
val sourceSets = the(SourceSetContainer::class)
val pluginSourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("dokkaJavadoc")
    archiveClassifier.set("javadoc")
    from("dokkaJavadoc.outputDirectory")
}

artifacts {
    add("archives", pluginSourcesJar)
}

val groupProperty = getPropertyOrFail("group")
val versionProperty = getPropertyOrFail("version")
val artifactId = getPropertyOrFail("id")
val mDescription = getPropertyOrFail("pluginDescription")

group = groupProperty
version = versionProperty

afterEvaluate {
    publishing {
        publications {
            register("release", MavenPublication::class.java) {
                setupPublication()
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        rootProject.ext["signingKeyId"].toString(),
        rootProject.ext["signingKey"].toString(),
        rootProject.ext["signingPassword"].toString(),
    )
    sign(publishing.publications)
}

fun MavenPublication.setupPublication() {
    groupId = groupProperty
    artifactId = artifactId
    version = versionProperty

    from(project.components["java"])
    artifact(pluginSourcesJar)
    artifact(javadocJar)

    pom {
        name.set(artifactId)
        description.set(mDescription)
        url.set("https://github.com/openmobilehub/omh-core")
        licenses {
            license {
                name.set("Apache-2.0 License")
                url.set("https://github.com/openmobilehub/omh-core/blob/main/LICENSE")
            }
        }

        developers {
            developer {
                id.set("Anwera64")
                name.set("Anton Soares")
            }
        }

        // Version control info - if you're using GitHub, follow the
        // format as seen here
        scm {
            connection.set("scm:git:github.com/openmobilehub/omh-core.git")
            developerConnection.set("scm:git:ssh://github.com/openmobilehub/omh-core.git")
            url.set("https://github.com/openmobilehub/omh-core")
        }
    }
}
