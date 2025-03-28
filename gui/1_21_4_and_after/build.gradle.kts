

dependencies{
        implementation(project(":api", configuration = "namedElements"))
        implementation(project(":alternatives:1_20_6_and_after", configuration = "namedElements"))

}
loom{
        accessWidenerPath.set(file("src/main/resources/tms-gui.accesswidener"))
}