plugins {
  java
  id("com.gradleup.shadow") version "9.3.0"
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
    val jdkVersion = 25
    val javaLanguageVersion = JavaLanguageVersion.of(jdkVersion)
    val javaVersion = JavaVersion.toVersion(jdkVersion)
    java {
      toolchain {
        languageVersion.set(javaLanguageVersion)
      }
      targetCompatibility = javaVersion
      sourceCompatibility = javaVersion
    }
    compileJava {
      options.release.set(jdkVersion)
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

      manifest {
        attributes(
          "Main-Class" to "org.goodgallery.Main"
        )
      }

      mergeServiceFiles()
    }
    withType<JavaExec> {
      systemProperty("file.encoding", "UTF-8")
      jvmArgs("--enable-native-access=ALL-UNNAMED")
    }
    test {
      useJUnitPlatform()
      testLogging {
        events("passed", "skipped", "failed")
      }
    }
  }
}
