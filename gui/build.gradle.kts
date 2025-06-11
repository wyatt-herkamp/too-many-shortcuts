plugins {
    id("java")
}

allprojects{
    if (project.path.startsWith(":gui:")){
        println("Adding gui resources to source set for project: ${project.path}")
        sourceSets{
            getByName("main"){
                resources{
                    srcDir("../src/main/resources")
                }
            }
        }
    }

}