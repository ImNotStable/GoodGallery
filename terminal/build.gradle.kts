plugins {
  application
}

dependencies {
  implementation(project(":common"))
  implementation("org.jetbrains:annotations:26.0.2-1")
  annotationProcessor("org.jetbrains:annotations:26.0.2-1")
  implementation("org.projectlombok:lombok:1.18.42")
  annotationProcessor("org.projectlombok:lombok:1.18.42")

  implementation("org.jline:jline:3.30.2")
}

application {
  mainClass.set("org.goodgallery.Main")
}