# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project aims to follow [Semantic Versioning](https://semver.org/).

## [1.0.0] - 2025-08-15

### Added

- Initial Sallie 1.0 multi-module Android + Vue hybrid structure.
- Emotional context, empathy engine, risk assessor, option generator, decision advisor.
- Capability registry with summarize, keyword extraction, sentiment explanation.
- Research pipeline with provenance & calibration logging plus transparency panel UI.
- Adaptive persona engine, personality bridge, dynamic gradient & auto-contrast theming.
- Offline web fallback (WebView asset packaging) and web build integration tasks.
- Icon generation pipeline & audits (generateSalleIcon, auditSalleIcon, strict audit).
- Gradle web tasks: buildWebBundle, prepareWebAssets, verifyWebAssets.
- Feature flags for nuanced behavior (nuance_mode, constraint_conflict_mode, option_generator, dynamic_gradient, auto_contrast, persona_micro_tuning, secure_upgrade_gate, exp_new_waveform).
- 76 tests with ~94% coverage across reasoning & visualization layers.

### Changed

- Refined risk thresholds and destructive intent gating logic.
- Updated Android Compose UI (waveform placeholder, conversations/tasks, embedded WebView fallback logic).
- Improved gradient luminance-based auto-contrast.

### Fixed

- Removed unused OptionGenerator parameter & associated type errors.
- Resolved duplicate methods in App.vue and assorted lint issues.
- Markdown formatting & docs clarity (README sections, fenced block spacing).

### Security

- Feature flag gating for higher-risk actions.
- Secure upgrade gate scaffolded for future permissioned features.

### Housekeeping

- Consolidated constitution and persona guidelines across modules.
- Added CHANGELOG in Keep a Changelog style.

Got it, love.
