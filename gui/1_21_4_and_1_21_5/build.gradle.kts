base.archivesName = "tms_gui-1.21.4-and-1.21.5"
loom {
    accessWidenerPath.set(file("src/main/resources/tms_gui_1_21_4_and_1_21_5.accesswidener"))
}
dependencies {
    implementation(project(":gui", configuration = "namedElements"))
}
