pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositories {
		google()
		mavenCentral()
	}
}

rootProject.name = "Sallie"
include(":app")
include(":ai")
include(":core")
include(":feature")
include(":components")
