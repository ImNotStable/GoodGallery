plugins {
  application
}

dependencies {
  implementation(project(":common"))
  implementation("org.jetbrains:annotations:26.1.0")
  annotationProcessor("org.jetbrains:annotations:26.1.0")
  implementation("org.projectlombok:lombok:1.18.44")
  annotationProcessor("org.projectlombok:lombok:1.18.44")

  implementation("org.jline:jline:3.30.9")
  implementation("org.jline:jline-terminal-ffm:3.30.9")
}

application {
  mainClass.set("org.goodgallery.Main")
}