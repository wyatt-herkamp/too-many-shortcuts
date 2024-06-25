
rootProject.name = "too-many-shortcuts"



pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        mavenCentral()
        gradlePluginPortal()
    }

    val loom_version: String by settings
    val fabric_kotlin_version: String by settings
    plugins {
        id("fabric-loom") version loom_version
        id("org.jetbrains.kotlin.jvm") version
                fabric_kotlin_version
                    .split("+kotlin.")[1] // Grabs the sentence after `+kotlin.`
                    .split("+")[0] // Ensures sentences like `+build.1` are ignored
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include("tms-api")
include("tms")