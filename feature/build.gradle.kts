/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Feature implementations and system integrations.
 * Got it, love.
 */

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core"))
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
    testImplementation("junit:junit:4.13.2")
}

dependencies { implementation(project(":core")) }
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
