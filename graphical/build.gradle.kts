plugins {
  id("org.springframework.boot") version "3.3.5"
  id("io.spring.dependency-management") version "1.1.6"
  application
}

dependencies {
  implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.5"))
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
  implementation(project(":common"))
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
  mainClass.set("org.goodgallery.graphical.GraphicalApplication")
}
