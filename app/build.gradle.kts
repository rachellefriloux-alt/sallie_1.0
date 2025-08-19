/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Main Android launcher application.
 * Got it, love.
 */

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.sallie.launcher"
    compileSdk = 31
    // Compatible with AGP 7.4.2
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sallie.launcher"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Product flavors for local-only vs cloud functionality
    flavorDimensions += "connectivity"
    productFlavors {
        create("localOnly") {
            dimension = "connectivity"
            applicationIdSuffix = ".local"
            versionNameSuffix = "-local"
            resValue("string", "app_name", "Sallie Local")
        }
        create("cloud") {
            dimension = "connectivity"
            applicationIdSuffix = ".cloud"
            versionNameSuffix = "-cloud"
            resValue("string", "app_name", "Sallie Cloud")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug { }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.1.1" }

    packagingOptions {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":ai"))
    implementation(project(":core"))
    implementation(project(":feature"))
    implementation(project(":components"))
    implementation(project(":ui"))
    implementation(project(":identity"))
    implementation(project(":onboarding"))
    implementation(project(":tone"))
    implementation(project(":personaCore"))

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    // Compose BOM for version alignment
    implementation(platform("androidx.compose:compose-bom:2022.10.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2022.10.00"))

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Android core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Cloud flavor specific dependencies
    "cloudImplementation"("com.google.firebase:firebase-firestore-ktx:24.10.0")
    "cloudImplementation"("com.google.firebase:firebase-auth-ktx:22.3.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Project modules
    implementation(project(":core"))
    implementation(project(":feature"))
    implementation(project(":ai"))
    implementation(project(":components"))
    implementation(project(":identity"))
    implementation(project(":onboarding"))
    implementation(project(":personaCore"))
    implementation(project(":responseTemplates"))
    implementation(project(":tone"))
    implementation(project(":ui"))
    implementation(project(":values"))

    // Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

tasks.withType<Test> { useJUnitPlatform() }