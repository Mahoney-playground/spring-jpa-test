plugins {
  kotlin("jvm") version "1.4.30"
  id("org.jetbrains.kotlin.plugin.spring").version("1.4.30")
  id("org.jetbrains.kotlin.plugin.jpa").version("1.4.30")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.data:spring-data-jpa:2.4.5")
  implementation("org.hibernate:hibernate-entitymanager:5.4.18.Final")
  implementation("org.postgresql:postgresql:42.2.19")
  implementation("org.flywaydb:flyway-core:7.5.4")

  testImplementation("io.kotest:kotest-runner-junit5:4.4.1")
  testImplementation("io.kotest:kotest-extensions-spring:4.4.1")
  testImplementation("io.kotest:kotest-extensions-testcontainers:4.4.1")
  testImplementation("org.springframework:spring-test:5.3.4")
  testImplementation("org.testcontainers:postgresql:1.15.2")
}

tasks {
  test {
    useJUnitPlatform()
  }
}
