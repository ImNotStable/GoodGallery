dependencies {
  implementation("org.jetbrains:annotations:26.1.0")
  annotationProcessor("org.jetbrains:annotations:26.1.0")
  implementation("org.projectlombok:lombok:1.18.46")
  annotationProcessor("org.projectlombok:lombok:1.18.46")

  implementation("org.xerial:sqlite-jdbc:3.53.0.0")
  implementation("com.h2database:h2:2.4.240")
  implementation("com.google.code.gson:gson:2.13.2")

  testImplementation(platform("org.junit:junit-bom:5.14.3"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
  jar {
    enabled = true
  }
  shadowJar {
    enabled = false
  }
}