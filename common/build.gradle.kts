dependencies {
  implementation("org.jetbrains:annotations:26.0.2-1")
  annotationProcessor("org.jetbrains:annotations:26.0.2-1")
  implementation("org.projectlombok:lombok:1.18.42")
  annotationProcessor("org.projectlombok:lombok:1.18.42")

  //implementation("com.zaxxer:HikariCP:7.0.2")
  //implementation("org.xerial:sqlite-jdbc:3.50.3.0")
  implementation("com.google.guava:guava:33.5.0-jre")
  implementation("com.google.code.gson:gson:2.13.2")
}

tasks {
  jar {
    enabled = true
  }
  shadowJar {
    enabled = false
  }
}