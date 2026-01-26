/*
 * Govinda ERP - Root Build Configuration
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring") apply false
    kotlin("plugin.jpa") apply false
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
    jacoco
}

val kotlinVersion: String by project
val springBootVersion: String by project
val postgresVersion: String by project
val flywayVersion: String by project
val testcontainersVersion: String by project
val mockkVersion: String by project
val springdocVersion: String by project

allprojects {
    group = "net.voytrex.govinda"
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
            mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
        }
    }

    dependencies {
        // Kotlin
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))

        // Jackson Kotlin module
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

        // Logging
        implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

        // Testing
        testImplementation("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "org.mockito")
        }
        testImplementation("io.mockk:mockk:$mockkVersion")
        testImplementation("org.assertj:assertj-core")
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-Xjsr305=strict",           // Strict null-safety
                "-Xjvm-default=all"          // Generate default methods in interfaces
            )
            jvmTarget = "21"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
        }
    }

    tasks.jacocoTestReport {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

// Root project tasks
tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}
