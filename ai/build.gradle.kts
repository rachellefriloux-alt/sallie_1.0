plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    // Gemini, ChatGPT, Copilot API clients (placeholders)
    // implementation("com.google.ai:gemini-client:1.0.0")
    // implementation("com.openai:chatgpt-client:1.0.0")
    // implementation("com.microsoft.copilot:copilot-client:1.0.0")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir(".") // allow top-level .kt files in module root
    }
}
