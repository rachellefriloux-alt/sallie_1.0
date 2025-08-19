/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Root build configuration for modular Android launcher.
 * Got it, love.
 */

// Top-level build file for Sallie 1.0
// Root build: alignment, verification, coverage, formatting – privacy-first (no new network code)
plugins {
    kotlin("jvm") version "1.6.10" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1" apply false
    jacoco
    id("org.jetbrains.kotlin.jvm") version "1.8.20" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1" apply false
}

val coverageMin: String = providers.environmentVariable("COVERAGE_MIN")
    .orElse(providers.gradleProperty("coverageMin"))
    .orElse("0.30")
    .get()
extensions.extraProperties["coverageMin"] = coverageMin

// Ensure a single aggregate check
val rootCheck = tasks.findByName("check") ?: tasks.register("check") {
    group = "verification"
    description = "Aggregate Salle verification (all subprojects + persona checks)."
}

// Apply verification to root project (choose only one implementation)
apply(from = "verification.gradle.kts")

subprojects {
    repositories {
        google()
        mavenCentral()
    plugins.withId("org.jetbrains.kotlin.jvm") {
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        apply(plugin = "jacoco")
        tasks.withType<Test>().configureEach { useJUnitPlatform() }
        if (tasks.findByName("jacocoTestReport") == null) {
            tasks.register<JacocoReport>("jacocoTestReport") {
                dependsOn(tasks.withType<Test>())
                reports { xml.required.set(true); html.required.set(true) }
                val classesDir = fileTree(layout.buildDirectory.dir("classes/kotlin/main"))
                classDirectories.setFrom(classesDir)
                sourceDirectories.setFrom(files("src/main/kotlin"))
                executionData.setFrom(fileTree(layout.buildDirectory.get().asFile) { include("**/jacoco/test*.exec", "**/jacoco/test/*.exec", "**/jacoco/*.exec") })
                onlyIf { executionData.files.any { it.exists() } }
            }
        }
        if (tasks.findByName("jacocoCoverageVerification") == null) {
            tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
                dependsOn(tasks.withType<Test>())
                val classesDir = fileTree(layout.buildDirectory.dir("classes/kotlin/main"))
                classDirectories.setFrom(classesDir)
                sourceDirectories.setFrom(files("src/main/kotlin"))
                executionData.setFrom(fileTree(layout.buildDirectory.get().asFile) { include("**/jacoco/test*.exec", "**/jacoco/test/*.exec", "**/jacoco/*.exec") })
                violationRules { rule { limit { minimum = coverageMin.toBigDecimal() } } }
                tasks.findByName("jacocoTestReport")?.let { mustRunAfter(it) }
                // Skip gracefully if no execution data (no tests)
                onlyIf { executionData.files.any { it.exists() } }
            }
        }
        tasks.matching { it.name == "check" }.configureEach {
            tasks.findByName("jacocoCoverageVerification")?.let { dependsOn(it) }
        }
    }
    plugins.withId("org.jetbrains.kotlin.android") {
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        apply(plugin = "jacoco")
        // Configure JUnit Platform for JVM unit tests inside Android modules
        tasks.withType<Test>().configureEach { useJUnitPlatform() }
        // Android unit test compiled class directories (debug variant typical)
        val kotlinClasses = layout.buildDirectory.dir("intermediates/javac/debug/classes")
        val altKotlinClasses = layout.buildDirectory.dir("tmp/kotlin-classes/debug")
        if (tasks.findByName("jacocoTestReport") == null) {
            tasks.register<JacocoReport>("jacocoTestReport") {
                dependsOn(tasks.withType<Test>())
                reports { xml.required.set(true); html.required.set(true) }
                classDirectories.setFrom(files(
                    fileTree(kotlinClasses) { include("**/*.class") },
                    fileTree(altKotlinClasses) { include("**/*.class") }
                ))
                sourceDirectories.setFrom(files("src/main/kotlin"))
                executionData.setFrom(fileTree(layout.buildDirectory.get().asFile) {
                    include("**/jacoco/test*.exec", "**/jacoco/test/*.exec", "**/jacoco/*.exec")
                })
                onlyIf { executionData.files.any { it.exists() } }
            }
        }
        if (tasks.findByName("jacocoCoverageVerification") == null) {
            tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
                dependsOn(tasks.withType<Test>())
                classDirectories.setFrom(files(
                    fileTree(kotlinClasses) { include("**/*.class") },
                    fileTree(altKotlinClasses) { include("**/*.class") }
                ))
                sourceDirectories.setFrom(files("src/main/kotlin"))
                executionData.setFrom(fileTree(layout.buildDirectory.get().asFile) {
                    include("**/jacoco/test*.exec", "**/jacoco/test/*.exec", "**/jacoco/*.exec")
                })
                violationRules { rule { limit { minimum = coverageMin.toBigDecimal() } } }
                tasks.findByName("jacocoTestReport")?.let { mustRunAfter(it) }
                onlyIf { executionData.files.any { it.exists() } }
            }
        }
        tasks.matching { it.name == "check" }.configureEach {
            tasks.findByName("jacocoCoverageVerification")?.let { dependsOn(it) }
        }
    }
    afterEvaluate {
        if (tasks.findByName("check") != null) {
            rootProject.tasks.named("check") { dependsOn(this@afterEvaluate.project.path + ":check") }
        }
    }
}

// Icon generation (local script only) – only attaches where preBuild exists
val generateSalleIcons by tasks.registering(Exec::class) {
    group = "sallie tools"
    description = "Generate launcher icons via local pipeline."
    commandLine("python3", "app/icon_pipeline/icon_pipeline.py")
}
tasks.matching { it.name == "preBuild" }.configureEach { dependsOn(generateSalleIcons) }

// Apply persona verification
apply(from = rootProject.file("verification.gradle.kts"))

gradle.projectsEvaluated {
    listOf("verifySalleFeatures", "verifySalleModules").forEach { tName ->
        tasks.findByName(tName)?.let { tasks.named("check") { dependsOn(tName) } }
    }
    tasks.findByName("verifySallePrivacy")?.let { tasks.named("check") { dependsOn("verifySallePrivacy") } }
    tasks.findByName("verifySalleLayering")?.let { tasks.named("check") { dependsOn("verifySalleLayering") } }
}

// Aggregate multi-module coverage (best-effort; does not fail if empty)
tasks.register<JacocoReport>("jacocoAggregateReport") {
    val testTasks = subprojects.flatMap { sp -> sp.tasks.matching { it.name.startsWith("test") } }
    dependsOn(testTasks)
    val execDataFiles = files(subprojects.map { sp ->
        sp.fileTree(sp.layout.buildDirectory.get().asFile) { include("**/jacoco/test.exec", "**/jacoco/test/*.exec") }
    })
    executionData.setFrom(execDataFiles)
    val classDirs = files(subprojects.map { sp -> sp.fileTree(sp.layout.buildDirectory.dir("classes/kotlin/main")) })
    val srcDirs = files(subprojects.map { sp -> sp.projectDir.resolve("src/main/kotlin") })
    classDirectories.setFrom(classDirs)
    sourceDirectories.setFrom(srcDirs)
    reports { xml.required.set(true); html.required.set(true); html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/aggregate")) }
    doFirst {
        if (execDataFiles.none { it.exists() }) {
            logger.lifecycle("No Jacoco exec data found; aggregate report will be empty but not failing.")
        }
    }
}
