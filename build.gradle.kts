plugins {
    java
    kotlin("jvm") version "1.9.25"  apply false
    kotlin("plugin.spring") version "1.9.25"  apply false
    id("org.springframework.boot") version "3.3.4" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
}

group = "org.example"
version = ""

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://repo.spring.io/milestone")
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    // Set an extra property "springAiVersion"
    extra["springAiVersion"] = "1.0.0-M6"

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
}
