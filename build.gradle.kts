plugins {
  id("java")
  id("com.gradleup.shadow") version "9.1.0"
}

group = "org.goodgallery"
version = "0.0.1"

allprojects {
  apply(plugin = "java")
  apply(plugin = "com.gradleup.shadow")

  repositories {
    mavenCentral()
  }

  dependencies {
    implementation("org.jetbrains:annotations:26.0.2-1")
    annotationProcessor("org.jetbrains:annotations:26.0.2-1")
    implementation("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("com.google.code.gson:gson:2.13.2")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  }

  tasks {
    test {
      useJUnitPlatform()
      testLogging {
        events("passed", "skipped", "failed")
      }
    }
  }
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "com.gradleup.shadow")

  repositories {
    mavenCentral()
  }

  dependencies {
    implementation(project(":"))
  }

  tasks {
    jar {
      enabled = false
    }
    assemble {
      dependsOn(shadowJar)
    }
    shadowJar {
      destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
      archiveBaseName.set("GoodGallery")
      archiveClassifier.set(project.name)
      archiveVersion.set("${rootProject.version}")

      manifest {
        attributes(mapOf("Main-Class" to "${group}.Main"))
      }
    }
  }
}