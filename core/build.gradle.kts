plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    // Firebase dependencies should be added to the app module if Android context is required
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
