plugins {
  application
}

dependencies {
  implementation(project(":common"))
  implementation("org.jetbrains:annotations:26.1.0")
  annotationProcessor("org.jetbrains:annotations:26.1.0")
  implementation("org.projectlombok:lombok:1.18.46")
  annotationProcessor("org.projectlombok:lombok:1.18.46")

  implementation("org.jline:jline:3.30.12")
  implementation("org.jline:jline-terminal-ffm:3.30.12")
}

application {
  mainClass.set("org.goodgallery.Main")
}