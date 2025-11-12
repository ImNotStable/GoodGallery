plugins {
  java
  id("com.gradleup.shadow") version "9.2.2"
}

group = "org.goodgallery"
version = "0.0.1"

tasks.jar.get().enabled = false

subprojects {
  apply(plugin = "java")
  apply(plugin = "com.gradleup.shadow")

  repositories {
    mavenCentral()
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
    }
    compileJava {
      options.release.set(javaLanguageVersion.asInt())
      options.encoding = Charsets.UTF_8.name()
      options.compilerArgs.plusAssign("-Xlint:deprecation")
    }
    assemble {
      dependsOn(shadowJar)
    }
    jar {
      enabled = false
    }
    shadowJar {
      archiveBaseName.set("GoodGallery")
      archiveClassifier.set(project.name)
      archiveVersion.set("${rootProject.version}")
      destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
      mergeServiceFiles()
      minimize()
    }
    test {
      useJUnitPlatform()
      testLogging {
        events("passed", "skipped", "failed")
      }
    }
  }
}
