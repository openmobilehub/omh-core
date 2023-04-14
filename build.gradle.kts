buildscript {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }

    dependencies {
        classpath(BuildPlugins.kotlin)
        classpath(BuildPlugins.detekt)
    }
}

subprojects {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }
}
