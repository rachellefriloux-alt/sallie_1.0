// Top-level build file for Sallie
plugins {
    id("com.android.application") version "8.12.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0" apply false
    id("jacoco")
}

// Allow dynamic coverage threshold via env COVERAGE_MIN or -PcoverageMin
val coverageMin: java.math.BigDecimal = (
    System.getenv("COVERAGE_MIN") ?: (findProperty("coverageMin") as String?) ?: "0.70"
).toBigDecimal()

subprojects {
    // Apply ktlint to Kotlin subprojects
    plugins.withId("org.jetbrains.kotlin.jvm") {
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        apply(plugin = "jacoco")
    }
    plugins.withId("org.jetbrains.kotlin.android") {
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        apply(plugin = "jacoco")
    }
    // Jacoco configuration & coverage verification for JVM modules
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<org.gradle.testing.jacoco.plugins.JacocoPluginExtension> { }
        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            finalizedBy("jacocoTestReport", "jacocoCoverageVerification")
        }
        if (tasks.findByName("jacocoTestReport") == null) {
            tasks.register<JacocoReport>("jacocoTestReport") {
                dependsOn(tasks.withType<Test>())
                reports {
                    xml.required.set(true)
                    html.required.set(true)
                }
                val sources = files("src/main/kotlin")
                val classesDir = fileTree(layout.buildDirectory.dir("classes/kotlin/main"))
                classDirectories.setFrom(classesDir)
                sourceDirectories.setFrom(sources)
                executionData.setFrom(fileTree(layout.buildDirectory.get().asFile) { include("**/jacoco/test.exec", "**/jacoco/test/*.exec") })
            }
        }
        if (tasks.findByName("jacocoCoverageVerification") == null) {
            tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
                dependsOn(tasks.withType<Test>())
                executionData.setFrom(fileTree(layout.buildDirectory.get().asFile) { include("**/jacoco/test.exec", "**/jacoco/test/*.exec") })
                val classesDir = fileTree(layout.buildDirectory.dir("classes/kotlin/main"))
                classDirectories.setFrom(classesDir)
                sourceDirectories.setFrom(files("src/main/kotlin"))
                violationRules {
                    rule {
                        limit {
                            minimum = coverageMin // default 70% line coverage (raise over time)
                        }
                    }
                }
                mustRunAfter(tasks.findByName("jacocoTestReport"))
            }
        }
        tasks.named("check") { dependsOn("jacocoCoverageVerification") }
    }
}

// Aggregate multi-module coverage (core + feature) into single report
tasks.register<JacocoReport>("jacocoAggregateReport") {
    dependsOn(subprojects.map { it.tasks.matching { t -> t.name == "test" } })
    val execDataFiles = files(subprojects.map { sp ->
        sp.fileTree(sp.layout.buildDirectory.get().asFile) { include("**/jacoco/test.exec", "**/jacoco/test/*.exec") }
    })
    executionData.setFrom(execDataFiles)
    val classDirs = files(subprojects.map { sp -> sp.fileTree(sp.layout.buildDirectory.dir("classes/kotlin/main")) }) 
    val srcDirs = files(subprojects.map { sp -> sp.projectDir.resolve("src/main/kotlin") })
    classDirectories.setFrom(classDirs)
    sourceDirectories.setFrom(srcDirs)
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/aggregate"))
    }
}

// Task to generate Salle icons using the Python script
val generateSalleIcons by tasks.registering(Exec::class) {
    group = "sallie tools"
    description = "Generates app icons using the Python icon pipeline."
    commandLine("python3", "app/icon_pipeline/icon_pipeline.py")
}

tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(generateSalleIcons)
}

// Apply Salle 1.0 specific verifications
apply(from = rootProject.file("verification.gradle.kts"))
