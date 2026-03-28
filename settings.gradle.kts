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
    val kotlin_version: String by settings
    plugins {
        id("net.fabricmc.fabric-loom") version loom_version
        kotlin("jvm") version kotlin_version
        kotlin("plugin.serialization") version kotlin_version
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(":core")
include(":gui")
include(":gui:26_1")


include("shortcuts")
include("shortcuts:26_1_and_after")
project(":shortcuts:26_1_and_after").name = "shortcuts-26_1_and_after"
