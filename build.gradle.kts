plugins {
    kotlin("jvm")
    id("fabric-loom")
    `maven-publish`
    java
    //kotlin("plugin.serialization")
}

group = property("maven_group")!!
version  = "${property("mod_version")}+mc.${property("minecraft_version")}"

repositories {
    exclusiveContent {
        forRepository {
            maven {
                url = uri("https://cursemaven.com")
            }
        }
        filter {
            includeGroup("curse.maven")
        }
    }
}

loom {
    accessWidenerPath.set(file("src/main/resources/too_many_shortcuts.accesswidener"))
}
dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
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
                pom{
                    url.set(property("source_url")!! as String);
                    developers{
                        developer{
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
            if (project.hasProperty("kingtux_dev_username") && project.hasProperty("kingtux_dev_password")){
                maven{
                    name = "kingtux_dev"
                    url = uri("https://repo.kingtux.dev/repositories/maven/fabric-mods/")
                    credentials {
                        username = project.property("kingtux_dev_username") as String
                        password = project.property("kingtux_dev_password") as String
                    }
                }
            }
        }
    }
}

kotlin{
    jvmToolchain(21)
}

java {
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
    withJavadocJar()
}
