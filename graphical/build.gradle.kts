plugins {
  id("org.openjfx.javafxplugin") version "0.1.0"
  application
}

javafx {
  version = "23.0.1"
  modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing")
}

application {
  mainClass.set("org.goodgallery.Main")
}

dependencies {
  implementation(project(":common"))
}

tasks.named<JavaExec>("run") {
  doFirst {
    println("Running GoodGallery with JavaFX...")
  }
}
