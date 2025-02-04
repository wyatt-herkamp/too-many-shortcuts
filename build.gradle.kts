plugins {
    kotlin("jvm")
    id("fabric-loom")
    `maven-publish`
    java
    kotlin("plugin.serialization")
    id("com.modrinth.minotaur") version "2.+"

}
group = property("maven_group")!!
version = "${property("mod_version")}+mc.${property("minecraft_version")}"
var mavenVersion = "${property("mod_version")}"
if (project.hasProperty("maven_version_extension")) {
    mavenVersion += "-${property("maven_version_extension")}"
}
repositories {}

loom {
    accessWidenerPath.set(file("src/main/resources/too_many_shortcuts.accesswidener"))
}
val yarn_mapping_version = "${property("minecraft_version")}+build.${property("yarn_mappings")}"
dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:$yarn_mapping_version:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
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

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                version = mavenVersion
                pom {
                    url.set(property("source_url")!! as String);
                    developers {
                        developer {
                            id.set("wyatt-herkamp")
                            name.set("Wyatt Herkamp")
                        }
                    }
                }
                artifact(remapJar) {
                    builtBy(remapJar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }

        // select the repositories you want to publish to
        repositories {
            mavenLocal()
            if (project.hasProperty("kingtux_dev_username") && project.hasProperty("kingtux_dev_password")) {
                println("Detected KingTux Dev Credentials")
                maven {
                    name = "kingtux_dev"
                    url = uri("https://repo.kingtux.dev/repositories/public/fabric-mods/")
                    credentials {
                        username = project.property("kingtux_dev_username") as String
                        password = project.property("kingtux_dev_password") as String
                    }
                }
            }
        }
    }
}
modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("too-many-shortcuts")
    versionNumber.set(version.toString())
    versionType.set(property("release_type").toString())
    uploadFile.set(tasks.remapJar)
    val supportedMinecraftVersions = property("supported_minecraft_versions").toString().split(",");
    gameVersions.addAll(supportedMinecraftVersions)
    loaders.add("fabric")
    dependencies {
        required.project("fabric-api")
        required.project("fabric-language-kotlin")
    }
    // LATEST_CHANGE_LOG is an environment variable that is set in the CI
    System.getenv("LATEST_CHANGE_LOG").let {
        project.logger.info("Got Change Log: \n $it")
        if (it != null) {
            changelog.set(it)
        }
    }
}
kotlin {
    jvmToolchain(21)
}

java {
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
    withJavadocJar()
}
