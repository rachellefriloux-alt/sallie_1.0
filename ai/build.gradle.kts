/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: AI orchestration and intelligence routing.
 * Got it, love.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
android {
    namespace = "com.sallie.ai"
    compileSdk = 34
    defaultConfig { minSdk = 26; targetSdk = 34 }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = false; buildConfig = true }
}

dependencies { implementation(project(":core")) }
