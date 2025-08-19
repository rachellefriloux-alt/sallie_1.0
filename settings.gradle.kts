/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Project settings and module inclusion.
 * Got it, love.
 */

pluginManagement {
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
	repositories {
		google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
		mavenCentral()
	}
}

rootProject.name = "Sallie"

// Core modules (JVM only for now)
// include(":app")  # temporarily disabled until Android plugins work
include(":ai")
include(":core")
include(":feature")
include(":components")

// Additional modules for complete architecture
include(":ui")
include(":identity")
include(":onboarding") 
include(":tone")
include(":personaCore")
include(":responseTemplates")
include(":values")
