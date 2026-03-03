plugins {
  application
}

dependencies {
  implementation(project(":common"))
  implementation("org.jetbrains:annotations:26.1.0")
  annotationProcessor("org.jetbrains:annotations:26.1.0")
  implementation("org.projectlombok:lombok:1.18.42")
  annotationProcessor("org.projectlombok:lombok:1.18.42")

  implementation("org.jline:jline:3.30.7")
  implementation("org.jline:jline-terminal-ffm:3.30.6")
}

application {
  mainClass.set("org.goodgallery.Main")
}