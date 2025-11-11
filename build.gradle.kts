plugins {
  id("java")
}

group = "org.goodgallery"
version = "0.0.1"

tasks {
}

allprojects {
  apply(plugin = "java")

  repositories {
    mavenCentral()
  }

  dependencies {
    implementation("org.jetbrains:annotations:26.0.2-1")
    annotationProcessor("org.jetbrains:annotations:26.0.2-1")
    implementation("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    //implementation("com.zaxxer:HikariCP:7.0.2")
    //implementation("org.xerial:sqlite-jdbc:3.50.3.0")
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("com.google.code.gson:gson:2.13.2")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  }

  tasks {
    val javaLanguageVersion = JavaLanguageVersion.of(25)
    val javaVersion = JavaVersion.VERSION_25
    java {
      toolchain {
        languageVersion.set(javaLanguageVersion)
      }
      targetCompatibility = javaVersion
      sourceCompatibility = javaVersion

      withJavadocJar()
      withSourcesJar()
    }
    compileJava {
      options.release.set(javaLanguageVersion.asInt())
      options.encoding = Charsets.UTF_8.name()

      options.compilerArgs.plusAssign("-Xlint:deprecation")
    }
    test {
      useJUnitPlatform()
      testLogging {
        events("passed", "skipped", "failed")
      }
    }
  }
}

subprojects {
  dependencies {
    implementation(rootProject)
  }
  tasks {
    jar {
      archiveBaseName.set("GoodGallery")
      archiveClassifier.set(project.name)
      archiveVersion.set("${rootProject.version}")
      destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }
  }
}
