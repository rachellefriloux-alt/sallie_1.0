/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: User onboarding and initial setup.
 * Got it, love.
 */

plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":identity"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}