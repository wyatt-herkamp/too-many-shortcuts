plugins {
    kotlin("jvm") version "2.1.10"
    id("java")
    kotlin("plugin.serialization")
    id("fabric-loom")

}

group = "dev.kingtux"
version = "${property("mod_version")}"
base.archivesName = "tms-extra-shortcuts-1.21.x"
repositories {
    mavenCentral()
}

dependencies {
    modImplementation(project(":api"))


    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
loom{
    //accessWidenerPath.set(file("src/main/resources/too_many_shortcuts_core.accesswidener"))
}
java {
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}
tasks {

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(getProperties())
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        from("LICENSE")
    }
}