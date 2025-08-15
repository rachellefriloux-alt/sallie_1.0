plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.sallie.launcher"
    // Updated to latest SDK to satisfy OldTargetApi lint and ensure forward compatibility
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sallie.launcher"
        compileSdk = 35
    targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    flavorDimensions("persona")
    productFlavors {
        create("creator") {
            dimension = "persona"
            resValue("string", "persona_mode", "creator")
        }
        create("mom") {
            dimension = "persona"
            resValue("string", "persona_mode", "mom")
        }
    }
}

dependencies {
        lint {
            baseline = file("lint-baseline.xml")
            abortOnError = true
            ignoreTestSources = true
            explainIssues = true
        }
    implementation(project(":ai"))
    implementation(project(":core"))
    implementation(project(":feature"))
    implementation(project(":components"))
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.work:work-runtime-ktx:2.10.3")
}

// Apply the Google services plugin to process app/google-services.json
// apply(plugin = "com.google.gms.google-services")

tasks.register("generateSallieIcons") {
    description = "Generate all persona × season × mood launcher icons"
    group = "build setup"

    doLast {
        println("💎 Generating SallieOS icon set…")
        exec {
            workingDir = file("$projectDir/icon_pipeline")
            commandLine("python3", "generate_icons.py")
        }
    }
}

tasks.named("preBuild") {
    dependsOn("generateSallieIcons")
}
