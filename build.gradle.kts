plugins {
    kotlin("jvm") version "2.3.0"
    id("fabric-loom")
    `maven-publish`
    java
    kotlin("plugin.serialization")
    id("com.modrinth.minotaur") version "2.+"

}
tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
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
    log4jConfigs.from("log4j-dev.xml")
}
dependencies {
    // Testing
    testImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    testImplementation(kotlin("test"))

    //modImplementation(project(":gui", configuration = "namedElements"))
    include(project(":core"))

    include(project(":gui"))
    include(project(":gui:1_21_11"))
    include(project(":shortcuts"))
    include(project(":shortcuts:shortcuts-1_21_and_after"))

    // So, you cant do a clean build with these options. However, you can only run the mod in development mode with these options.
    runtimeOnly(project(":gui", configuration = "namedElements"))
    runtimeOnly(project(":gui:1_21_11", configuration = "namedElements"))
    runtimeOnly(project(":shortcuts", configuration = "namedElements"))
    runtimeOnly(project(":shortcuts:shortcuts-1_21_and_after", configuration = "namedElements"))

    implementation(project(":shortcuts", configuration = "namedElements"))
    implementation(project(":gui", configuration = "namedElements"))

}

allprojects {
    val yarnMappingVersion = "${property("minecraft_version")}+build.${property("yarn_mappings")}"
    apply(plugin = "fabric-loom")
    apply(plugin = "java")
    apply(plugin = "kotlin")
    version = property("mod_version") as String
    group = property("maven_group") as String
    dependencies {
        minecraft("com.mojang:minecraft:${property("minecraft_version")}")
        mappings("net.fabricmc:yarn:$yarnMappingVersion:v2")
        modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

        modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
        testImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
        testImplementation(kotlin("test"))
        if (project.path == ":core") {
            return@dependencies
        }
        annotationProcessor(implementation(project(":core", configuration = "namedElements")) as Dependency)
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
        test {
            useJUnitPlatform()
        }
    }

}
tasks {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                version = mavenVersion
                pom {
                    name.set("Too Many Shortcuts")
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
