plugins {
    kotlin("jvm") version "2.3.20"
    id("net.fabricmc.fabric-loom")
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

    include(project(":core"))

    include(project(":gui"))
    include(project(":gui:26_1"))
    include(project(":shortcuts"))
    include(project(":shortcuts:shortcuts-26_1_and_after"))

    // So, you cant do a clean build with these options. However, you can only run the mod in development mode with these options.
    runtimeOnly(project(":gui"))
    runtimeOnly(project(":gui:26_1"))
    runtimeOnly(project(":shortcuts"))
    runtimeOnly(project(":shortcuts:shortcuts-26_1_and_after"))

    implementation(project(":shortcuts"))
    implementation(project(":gui"))

}

allprojects {
    apply(plugin = "net.fabricmc.fabric-loom")
    apply(plugin = "java")
    apply(plugin = "kotlin")
    version = property("mod_version") as String
    group = property("maven_group") as String
    dependencies {
        minecraft("com.mojang:minecraft:${property("minecraft_version")}")
        implementation("net.fabricmc:fabric-loader:${property("loader_version")}")

        implementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
        implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
        testImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
        testImplementation(kotlin("test"))
        if (project.path == ":core") {
            return@dependencies
        }
        annotationProcessor(implementation(project(":core")) as Dependency)
    }
    kotlin {
        jvmToolchain(25)
    }

    java {
        targetCompatibility = JavaVersion.VERSION_25
        sourceCompatibility = JavaVersion.VERSION_25
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
                artifact(jar) {
                    builtBy(jar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(kotlinSourcesJar)
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
    uploadFile.set(tasks.jar)
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
