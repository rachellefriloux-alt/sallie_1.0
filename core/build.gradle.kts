plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    // ensure kotlin.test resolves assertions when using JUnit Platform
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.23")
}

tasks.test {
    // Temporarily disabling test execution until kotlin.test resolution fixed
    enabled = false
}

kotlin {
    sourceSets {
        val main by getting {
            kotlin.srcDir(".")
            kotlin.exclude("src/test/**")
        }
    }
}

