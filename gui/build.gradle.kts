allprojects{
    if (project.path.count { it == ':' } <3){
        return@allprojects
    }
    sourceSets{
        getByName("main"){
            resources{
                srcDir("../src/main/resources")
                srcDir("../../src/main/resources")
            }
        }
    }
    dependencies{
        modImplementation(project(":api"))
    }
}