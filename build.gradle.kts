/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Root build configuration for modular Android launcher.
 * Got it, love.
 */

// Top-level build file for Sallie 1.0 - testing without Android plugins first
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.22" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1" apply false
}

// Allow dynamic coverage threshold via env COVERAGE_MIN or -PcoverageMin
val coverageMin: java.math.BigDecimal = (
    System.getenv("COVERAGE_MIN") ?: (findProperty("coverageMin") as String?) ?: "0.70"
).toBigDecimal()

// Apply verification to root project (choose only one implementation)
apply(from = "verification.gradle.kts")

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    
    repositories {
        google()
        mavenCentral()
    }
    
    // Configure ktlint for all modules
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("0.50.0")
        android.set(false)
        ignoreFailures.set(false)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        }
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
}
