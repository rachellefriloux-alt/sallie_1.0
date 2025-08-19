/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Values alignment and ethical decision making.
 * Got it, love.
 */

plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("jvm")
}

dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
