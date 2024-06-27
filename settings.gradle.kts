
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
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

