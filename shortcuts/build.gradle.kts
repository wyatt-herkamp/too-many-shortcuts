plugins {
    id("java")
}

allprojects {
    loom {
        if (properties.containsKey("access_widener")) {
            accessWidenerPath.set(file("src/main/resources/${properties.get("access_widener")}.accesswidener"))
        }
    }
    if (project.path.startsWith(":shortcuts:")) {
        println("Adding shortcuts resources to source set for project: ${project.path}")
        sourceSets {
            getByName("main") {
                resources {
                    srcDir("../src/main/resources")
                }
            }
        }
    }

}