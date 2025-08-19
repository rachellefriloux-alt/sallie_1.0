/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: User onboarding and initial setup.
 * Got it, love.
 */

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
    implementation(project(":core"))
    implementation(project(":identity"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
