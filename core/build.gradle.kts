/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Core data models, memory management, and system state.
 * Got it, love.
 */

plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.sallie.launcher.SallieDemoKt")
}

dependencies {
    implementation(project(":personaCore"))
    implementation(project(":tone"))
    implementation(project(":values"))
    implementation(project(":responseTemplates"))
    implementation(project(":identity"))
    implementation(project(":onboarding"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

