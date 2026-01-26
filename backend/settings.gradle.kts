/*
 * Govinda ERP - Settings
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

rootProject.name = "govinda"

// Module includes
include(
    "govinda-common",
    "govinda-masterdata",
    "govinda-product",
    "govinda-contract",
    "govinda-premium",
    "govinda-billing",
    "govinda-app"
)

// Plugin management
pluginManagement {
    val kotlinVersion: String by settings
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.jpa") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

// Dependency resolution
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}
