plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core"))
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
    testImplementation("junit:junit:4.13.2")
}
