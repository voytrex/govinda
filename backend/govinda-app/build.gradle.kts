/*
 * Govinda ERP - Main Application
 * Spring Boot application entry point
 */

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

val springdocVersion: String by project

dependencies {
    // Internal modules
    implementation(project(":govinda-common"))
    implementation(project(":govinda-masterdata"))
    implementation(project(":govinda-product"))
    implementation(project(":govinda-contract"))
    implementation(project(":govinda-premium"))
    implementation(project(":govinda-billing"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    // Development
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
}

springBoot {
    mainClass.set("net.voytrex.govinda.GovindaApplicationKt")
}

tasks.bootJar {
    archiveBaseName.set("govinda")
    archiveVersion.set(project.version.toString())
}
