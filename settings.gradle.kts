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
        id("fabric-loom") version loom_version
        kotlin("jvm") version kotlin_version
        kotlin("plugin.serialization") version kotlin_version
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(":mixin-helpers")
include(":gui")
include(":gui:1_21_4_and_1_21_5")
include(":gui:1_21_3")
include(":gui:1_21_6")