plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.sallie.ai"
    compileSdk = 34
    defaultConfig { minSdk = 26; targetSdk = 34 }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = false; buildConfig = true }
}

dependencies { 
    implementation(project(":core"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
}
