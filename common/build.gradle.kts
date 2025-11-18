dependencies {
  implementation("org.jetbrains:annotations:26.0.2-1")
  annotationProcessor("org.jetbrains:annotations:26.0.2-1")
  implementation("org.projectlombok:lombok:1.18.42")
  annotationProcessor("org.projectlombok:lombok:1.18.42")

  implementation("org.xerial:sqlite-jdbc:3.50.3.0")
  implementation("com.h2database:h2:2.4.240")
  implementation("com.google.code.gson:gson:2.13.2")

  testImplementation(platform("org.junit:junit-bom:5.13.4"))
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