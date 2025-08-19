plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.sallie.launcher.SallieDemoKt")
}

dependencies {
    implementation(project(":personaCore"))
    implementation(project(":tone"))
    implementation(project(":values"))
    implementation(project(":responseTemplates"))
    implementation(project(":identity"))
    implementation(project(":onboarding"))
    
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
    testImplementation("junit:junit:4.13.2")
}
