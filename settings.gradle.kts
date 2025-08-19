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
	repositories {
		google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "sallie_1.0"

// include(":app")
include(":app")
rootProject.name = "Sallie"

// Core modules (Android app temporarily excluded due to repository access issues)
// include(":app")
include(":ai")
include(":core")
include(":feature")
include(":ai")
include(":components")
include(":identity")
include(":onboarding")
include(":personaCore")
include(":responseTemplates")
include(":tone")
include(":ui")
include(":values")

// Additional modules for complete architecture
include(":ui")
include(":identity")
include(":onboarding")
include(":tone")
include(":personaCore")
include(":responseTemplates")
include(":values")
