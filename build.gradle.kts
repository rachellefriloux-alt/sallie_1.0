/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Root build configuration for modular Android launcher.
 * Got it, love.
 */

// Top-level build file for Sallie 1.0
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.20" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1" apply false
}

// Allow dynamic coverage threshold via env COVERAGE_MIN or -PcoverageMin
val coverageMin: java.math.BigDecimal = (
    System.getenv("COVERAGE_MIN") ?: (findProperty("coverageMin") as String?) ?: "0.70"
).toBigDecimal()

// Apply verification to root project (choose only one implementation)
apply(from = "verification.gradle.kts")

subprojects {
    repositories {
        google()
        mavenCentral()
    }
}
