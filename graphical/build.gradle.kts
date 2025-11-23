plugins {
  id("org.springframework.boot") version "4.0.0"
  id("io.spring.dependency-management") version "1.1.7"
  application
}

dependencies {
  implementation(project(":common"))
  implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.0"))
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
  mainClass.set("org.goodgallery.Main")
}
