plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    // Compose and Material3 dependencies are only for Android modules
}

kotlin {
    sourceSets.main {
        kotlin.srcDir(".")
    }
}
