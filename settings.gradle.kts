// Explicit settings for Sallie multi-module workspace
pluginManagement {
    repositories {
        google()
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
