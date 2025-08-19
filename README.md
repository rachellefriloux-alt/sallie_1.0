# Sallie

Sallie is a next-generation Android launcher and personal AI system, designed to be an extension of you. She combines the best features of Alexa, Siri, Gemini, and ChatGPT, with advanced emotional intelligence, memory, and system control. Sallie is modular, extensible, and built for future upgrades.

## Features
- Multi-AI orchestration (Gemini, ChatGPT, Copilot)
- Emotional intelligence and adaptive personality
- Full device control and automation
- Dynamic theming and UI customization
- Persistent memory and context
- Voice, chat, and system integrations
- Modular skill/plugin system
 - Situation analysis & task orchestration
 - Bias interception & dignity protocols
 - Draft management & routine automation
 - Upgrade matrix & impact logging
 - Values reflection & legacy alignment
 - Feature registry with diagnostics

## Tech Stack
- Kotlin
- Jetpack Compose
- Firebase (Firestore & Auth)
- Gradle version catalogs

## Getting Started
1. Clone the repo
2. Open in Android Studio
3. Build and run the app

### Module Tests (Kotlin JVM feature layer)
Run feature module tests:

```
./gradlew :feature:test
```

The test suite currently covers task selection logic, draft lifecycle, routine execution, and device permission gating.

### Core & Feature Test Coverage
Continuous Integration runs core + feature tests with ktlint and Jacoco coverage.

Badges (placeholders until first CI + Codecov run):

![CI](https://img.shields.io/github/actions/workflow/status/your-org/sallie/ci.yml?branch=main)
![Coverage](https://img.shields.io/badge/coverage-aggregate--jacoco-%2300aa88)
![Codecov](https://img.shields.io/badge/codecov-report-blue)
![Ktlint](https://img.shields.io/badge/code%20style-ktlint-blue)

Aggregate coverage: `./gradlew jacocoAggregateReport` (override threshold with `COVERAGE_MIN=0.75`).
Add real coverage badge linking to Codecov after uploading first report.

## Folder Structure
- `app/` - Launcher app module
- `ai/` - AI orchestration and skills
- `core/` - Core logic, memory, context
- `feature/` - Modular features and integrations
- `feature/FeatureRegistry.kt` - Central access point for feature singletons & summary metrics
- `components/` - UI components
