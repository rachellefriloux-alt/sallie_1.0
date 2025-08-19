/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: UI components and styling system.
 * Got it, love.
 */

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core"))
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
    testImplementation("junit:junit:4.13.2")
android {
    namespace = "com.sallie.components"
    compileSdk = 34
    defaultConfig { minSdk = 26; targetSdk = 34 }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = false }
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
