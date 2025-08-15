plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.sallie.launcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sallie.launcher"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    implementation(project(":ai"))
    implementation(project(":core"))
    implementation(project(":feature"))
    implementation(project(":components"))
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.9.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
}

// ...existing code...

val salleMood: String = findProperty("SALLE_MOOD")?.toString() ?: "calm"

// ...existing code...

tasks.register<Exec>("generateSalleIcon") {
    group = "build setup"
    description = "Generates Salle's glowing mood orb icon before resources are processed."
    commandLine("python", "app/icon_pipeline/generate_mood_orb.py", findProperty("SALLE_MOOD")?.toString() ?: "calm", projectDir.toString())
}

tasks.register("auditSalleIcon") {
    group = "verification"
    description = "Verifies Salle's launcher icon purity and build timestamp."
    doLast {
        val mipmapBase = File("$projectDir/src/main/res/")
        val densities = listOf("mipmap-mdpi", "mipmap-hdpi", "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi")
        val now = System.currentTimeMillis()
        densities.forEach { dpi ->
            val iconFile = File(mipmapBase, "$dpi/ic_launcher.png")
            if (!iconFile.exists()) throw GradleException("Missing launcher icon in $dpi")
            val lastModified = iconFile.lastModified()
            if (now - lastModified > 1000 * 60 * 10) throw GradleException("Icon in $dpi is stale or manually overwritten!")
        }
    }
}

tasks.register("auditSalleIconStrict") {
    group = "verification"
    description = "Strict audit: checks for non-pipeline PNGs and correct mood property."
    doLast {
        val mipmapBase = File("$projectDir/src/main/res/")
        val densities = listOf("mipmap-mdpi", "mipmap-hdpi", "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi")
        val allowedMoods = setOf("calm", "focused", "energized", "reflective", "guarded", "celebratory", "hopeful", "melancholy", "playful", "resolute")
        val mood = findProperty("SALLE_MOOD")?.toString() ?: "calm"
        if (mood !in allowedMoods) throw GradleException("Invalid mood: $mood")
        densities.forEach { dpi ->
            val dir = File(mipmapBase, dpi)
            dir.listFiles()?.forEach { file ->
                if (file.name != "ic_launcher.png") throw GradleException("Unexpected PNG in $dpi: ${file.name}")
            }
        }
    }
}

tasks.named("preBuild") {
    dependsOn("generateSalleIcon")
    dependsOn("auditSalleIcon")
    dependsOn("auditSalleIconStrict")
}
