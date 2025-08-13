// Top-level build file for Sallie
plugins {
    id("com.android.application") version "8.2.2" apply false
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
        extensions.configure<org.gradle.testing.jacoco.plugins.JacocoTaskExtension> { }
        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            finalizedBy("jacocoTestReport", "jacocoCoverageVerification")
        }
        val jacocoTestReport = tasks.register<JacocoReport>("jacocoTestReport") {
            dependsOn(tasks.withType<Test>())
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
            val sources = files("src/main/kotlin")
            val classesDir = fileTree("build/classes/kotlin/main")
            classDirectories.setFrom(classesDir)
            sourceDirectories.setFrom(sources)
            executionData.setFrom(fileTree(project.buildDir) { include("**/jacoco/test.exec", "**/jacoco/test/*.exec") })
        }
        tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
            dependsOn(tasks.withType<Test>())
            executionData.setFrom(fileTree(project.buildDir) { include("**/jacoco/test.exec", "**/jacoco/test/*.exec") })
            val classesDir = fileTree("build/classes/kotlin/main")
            classDirectories.setFrom(classesDir)
            sourceDirectories.setFrom(files("src/main/kotlin"))
            violationRules {
                rule {
                    limit {
                        minimum = coverageMin // default 70% line coverage (raise over time)
                    }
                }
            }
            mustRunAfter(jacocoTestReport)
        }
        tasks.named("check") { dependsOn("jacocoCoverageVerification") }
    }
}

// Aggregate multi-module coverage (core + feature) into single report
tasks.register<JacocoReport>("jacocoAggregateReport") {
    dependsOn(subprojects.map { it.tasks.matching { t -> t.name == "test" } })
    val execDataFiles = files(subprojects.map { sp ->
        sp.fileTree(sp.buildDir) { include("**/jacoco/test.exec", "**/jacoco/test/*.exec") }
    })
    executionData.setFrom(execDataFiles)
    val classDirs = files(subprojects.map { sp -> sp.fileTree("${sp.buildDir}/classes/kotlin/main") })
    val srcDirs = files(subprojects.map { sp -> sp.projectDir.resolve("src/main/kotlin") })
    classDirectories.setFrom(classDirs)
    sourceDirectories.setFrom(srcDirs)
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/aggregate"))
    }
}
