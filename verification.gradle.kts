// verification.gradle.kts

tasks.register<VerifySalleFeatures>("verifySalleModules") {
    requiredModules.set(
        listOf(
            // ":app",  // Commented out for JVM demo
            ":ai",
            ":core",
            ":feature",
            ":components",
            ":personaCore",
            ":identity",
            ":tone",
            ":ui",
            ":onboarding",
            ":responseTemplates",
            ":values"
        )
    )

    // These stay as‑is from your current enforcement rules:
    forbiddenImports.set(
        listOf(
            "java.net.URL",
            "java.net.HttpURLConnection"
            // …plus any others you already block
        )
    )

    personaHeaderMarker.set("// 🛡 SALLE PERSONA ENFORCED 🛡")
    personaSlogan.set("Loyal, Modular, Audit‑Proof.")
    maxMainActivityLines.set(500) 
    enforcePersonaHeaders.set(true)
    baseDirPath.set(project.rootDir.absolutePath)
    kotlinSources.set(fileTree(project.rootDir) {
        include("**/*.kt")
    })
}

// Privacy guard task: scans for disallowed network/analytics symbols.
tasks.register<VerifySallePrivacy>("verifySallePrivacy") {
    baseDirPath.set(project.rootDir.absolutePath)
    bannedTokens.set(listOf(
        "HttpURLConnection(",
        "OkHttpClient(",
        "Retrofit.Builder",
        "FirebaseAnalytics",
        "GoogleAnalytics",
        "AnalyticsTracker",
        "Socket("
    ))
    kotlinSources.set(fileTree(project.rootDir) { include("**/*.kt") })
}

tasks.register<VerifySalleLayering>("verifySalleLayering") {
    baseDirPath.set(project.rootDir.absolutePath)
    layerOrder.set(listOf(
        // ":app",  // Commented out for JVM demo
        ":feature",
        ":components",
        ":ui",
        ":personaCore",
        ":identity",
        ":onboarding",
        ":tone",
        ":responseTemplates",
        ":values",
        ":ai", // ai near bottom but above core if it uses core logic only
        ":core"
    ))
    buildFiles.set(fileTree(project.rootDir) { include("**/build.gradle.kts", "**/build.gradle") })
}
