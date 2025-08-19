/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Project settings and module inclusion.
 * Got it, love.
 */

// Explicit settings for Sallie multi-module workspace
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "sallie_1.0"

// Core modules
include(":app")
include(":ai")
include(":core")
include(":feature")
include(":components")
include(":identity")
include(":onboarding")
include(":personaCore")
include(":responseTemplates")
include(":tone")
include(":ui")
include(":values")