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

tasks.register("installPreCommitHook", Copy::class) {
    from("tools/scripts/pre-commit")
    into(".git/hooks")
    fileMode = 0b000_111_111_111
}

tasks {
    val installPreCommitHook by existing
    getByName("prepareKotlinBuildScriptModel").dependsOn(installPreCommitHook)
}
