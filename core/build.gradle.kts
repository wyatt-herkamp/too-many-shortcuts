plugins {
    kotlin("jvm") version "2.1.21"
    id("java")
    kotlin("plugin.serialization")
    `maven-publish`
}
loom {
    accessWidenerPath.set(file("src/main/resources/tms_core.accesswidener"))
}
repositories {
    mavenCentral()
}
group = property("maven_group")!!
version = "${property("mod_version")}+mc.${property("minecraft_version")}"
var mavenVersion = "${property("mod_version")}"
if (project.hasProperty("maven_version_extension")) {
    mavenVersion += "-${property("maven_version_extension")}"
}
tasks {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                version = mavenVersion
                pom {
                    name.set("Too Many Shortcuts Core")
                    artifactId = "tms_core"
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