plugins {
    kotlin("jvm") version "2.1.21"
    id("java")
    kotlin("plugin.serialization")

}
loom {
    accessWidenerPath.set(file("src/main/resources/tms_core.accesswidener"))
}
repositories {
    mavenCentral()
}

