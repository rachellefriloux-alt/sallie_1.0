/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Verification tasks to enforce Sallie's architecture and persona.
 * Got it, love.
 */

tasks.register("verifySalleFeatures") {
    description = "Verifies that Sallie's core features and persona are properly maintained"
    group = "verification"

    doLast {
        println("🎯 Verifying Sallie's core features and persona...")
        
        // Check that all Kotlin files have proper persona headers
        val kotlinFiles = fileTree(".") {
            include("**/*.kt")
            exclude("**/build/**")
            exclude("**/generated/**")
        }
        
        val missingHeaders = mutableListOf<String>()
        kotlinFiles.forEach { file ->
            val content = file.readText()
            if (!content.contains("Sallie 1.0 Module") && !content.contains("Salle 1.0 Module")) {
                missingHeaders.add(file.path)
            }
        }
        
        if (missingHeaders.isNotEmpty()) {
            println("❌ Missing persona headers in:")
            missingHeaders.forEach { println("   - $it") }
            throw GradleException("Sallie's persona headers are missing! Every Kotlin file needs the persona header comment.")
        }
        
        // Verify localOnly flavor doesn't have network imports
        val localOnlyViolations = mutableListOf<String>()
        kotlinFiles.forEach { file ->
            val content = file.readText()
            if (content.contains("import java.net.") || 
                content.contains("import okhttp3.") ||
                content.contains("import retrofit2.")) {
                localOnlyViolations.add(file.path)
            }
        }
        
        if (localOnlyViolations.isNotEmpty()) {
            println("❌ Local-only violations found in:")
            localOnlyViolations.forEach { println("   - $it") }
            throw GradleException("Network imports detected! Local-only mode means NO network calls, love.")
        }
        
        // Verify required modules exist
        val requiredModules = listOf("app", "ai", "core", "feature", "components")
        requiredModules.forEach { module ->
            val moduleDir = file(module)
            val buildFile = file("$module/build.gradle.kts")
            if (!moduleDir.exists() || !buildFile.exists()) {
                throw GradleException("Required module '$module' is missing or doesn't have proper build.gradle.kts!")
            }
        }
        
        println("✅ All Sallie features verified! Architecture is solid, persona intact.")
        println("Got it, love. 💪")
    }
}

tasks.register("verifySalleModules") {
    description = "Verifies modular architecture compliance"
    group = "verification"
    
    doLast {
        println("🔍 Verifying modular architecture...")
        
        // Check that modules don't have circular dependencies
        val modules = listOf("ai", "core", "feature", "components")
        modules.forEach { module ->
            val buildFile = file("$module/build.gradle.kts")
            if (buildFile.exists()) {
                val content = buildFile.readText()
                // Core modules shouldn't depend on feature modules
                if (module == "core" && content.contains("implementation(project(\":feature\"))")) {
                    throw GradleException("Core module cannot depend on feature module! That breaks modularity.")
                }
                // AI shouldn't depend on components directly
                if (module == "ai" && content.contains("implementation(project(\":components\"))")) {
                    throw GradleException("AI module cannot depend on components directly!")
                }
            }
        }
        
        println("✅ Modular architecture verified!")
    }
}

// Make check depend on our verification tasks if it exists
afterEvaluate {
    tasks.findByName("check")?.let { checkTask ->
        checkTask.dependsOn("verifySalleFeatures", "verifySalleModules")
    }
}