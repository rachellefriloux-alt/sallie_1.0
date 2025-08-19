<template>
  <div id="app" :style="computedBackgroundStyle" :class="{ 'dynamic-gradient-active': dynamicGradientEnabled }">
    <consent-dialog v-if="consentRequired" :reasons="consentReasons" :dependency-diff="dependencyDiff" @accept="grantConsent" @decline="declineConsent" />
    <template v-else>
      <onboarding-core v-if="!onboarded" @onboarded="handleOnboarded" />
      <div v-else>
        <div class="scenario-bar">
          <button v-for="scenario in scenarios" :key="scenario" @click="respondToUser(userProfile.name, scenario)">{{ capitalize(scenario) }}</button>
        </div>
        <div style="margin-bottom:12px; display:flex; gap:8px;">
          <input v-model="adHocGoal" placeholder="Enter goal e.g. summarize &quot;text here&quot;" style="flex:1; padding:6px 8px; border:1px solid #ccc; border-radius:6px;" />
          <button style="background:#4f46e5; color:#fff; border:none; padding:6px 12px; border-radius:6px; cursor:pointer;" @click="executeGoal">Run</button>
        </div>
        <safe-svg :svg="patternSVG" component-class="visual-bg" />
        <safe-svg :svg="avatarSVG" component-class="persona-avatar" />
        <emotion-overlay :emotion="emotion" :message="emotionMessage" />
        <insight-card :insight="insight" />
        <div style="position:absolute; top:12px; left:12px; display:flex; gap:8px; z-index:50;">
          <button style="background:#222; color:#fff; border:1px solid #444; padding:6px 10px; border-radius:6px; cursor:pointer; font-size:12px;" @click="toggleTransparency">Transparency</button>
        </div>
        <div v-if="hasFullAccess" style="position:relative;">
          <voice-console />
          <safe-svg :svg="emotionMeterSVG" component-class="emotion-meter" />
          <safe-svg :svg="voiceWaveformSVG" component-class="voice-waveform" />
          <task-panel :tasks="tasks" />
          <feature-flags-panel @updated="flagsUpdated" />
          <plugin-registry-panel />
          <transparency-panel :open="showTransparency" :decision="lastDecision" :provenance="lastProvenance" :calibration="riskCalibration" @close="showTransparency=false" />
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import OnboardingCore from './onboarding/onboardingCore.vue';
import EmotionOverlay from './ui/components/EmotionOverlay.vue';
import VoiceConsole from './ui/components/VoiceConsole.vue';
import TaskPanel from './ui/components/TaskPanel.vue';
import InsightCard from './ui/components/InsightCard.vue';
import ConsentDialog from './ui/components/ConsentDialog.vue';
import FeatureFlagsPanel from './ui/components/FeatureFlagsPanel.vue';
import PluginRegistryPanel from './ui/components/PluginRegistryPanel.vue';
import { adaptivePersonaEngine } from './core/AdaptivePersonaEngine';
import { pluginRegistry } from './core/PluginRegistry';

import { generateTheme } from './ui/visual/themeGenerator';
let highContrastFn = null; let reducedMotionFn = null;
import { generatePatternSVG, generateAvatarSVG } from './ui/visual/svgGenerator';
import { generateEmotionMeterSVG } from './ui/visual/visualizationUtils';
import { generateWaveformSVG } from './ui/visual/voiceVisualUtils';
import { injectVisualCSS } from './ui/visual/cssInjector';
import SafeSvg from './ui/components/SafeSvg.vue';
import { currentFingerprintSegments } from './core/fingerprintRuntime';
import { initConsent, recordConsent } from './core/runtimeConsent';
import { isEnabled, experimentalBlocked } from './core/featureFlags';
import { emotionMap } from './ai/utils/emotionMap';
import { getTraits, onTraits } from './core/PersonalityBridge';
import TransparencyPanel from './ui/components/TransparencyPanel.vue';
import { runGoal, calibrationSummary } from './core/ResearchService';

export default {
  name: 'App',
  components: { OnboardingCore, EmotionOverlay, VoiceConsole, TaskPanel, InsightCard, SafeSvg, ConsentDialog, FeatureFlagsPanel, PluginRegistryPanel, TransparencyPanel },
  data() {
    return {
      onboarded: false,
      emotion: 'calm',
      emotionMessage: 'All clear.',
      detectedScenario: null,
      scenarioVerificationPending: false,
      scenarioCandidates: [],
      insight: 'Welcome to Sallie!',
      userProfile: null,
      hasFullAccess: false,
      tasks: [],
      theme: { text: '#333', background: '#f7f7fa' }, // Default theme
      patternSVG: '<svg></svg>',
      avatarSVG: '<svg></svg>',
      emotionMeterSVG: '<svg></svg>',
      voiceWaveformSVG: '<svg></svg>',
      scenarios: ['stressed','focused','overwhelmed','conflict','celebration','creative','reflective','repair','choose','support','motivate','soothe','clarify'],
      consentRequired: false,
      consentReasons: [],
      experimentalWaveformBlocked: false,
      dependencyDiff: { added: [], removed: [], changed: [] },
      volatility: 0,
      dynamicGradientEnabled: false,
      showTransparency: false,
      lastDecision: null,
      lastProvenance: [],
      riskCalibration: null,
      adHocGoal: ''
    };
  },
  computed: {
    computedBackgroundStyle(){
      const base = { color: this.theme.text };
      if (!this.theme.gradient) { return { ...base, background: this.theme.background || '#111' }; }
      return { ...base, background: this.theme.gradient };
    }
  },
  created() {
    // Simple initialization without dependencies for now
    console.log('Sallie initialized');
  },
  methods: {
    capitalize(s) { return s.charAt(0).toUpperCase() + s.slice(1); },
    updateVisuals() {
      console.log('Updating visuals for emotion:', this.emotion);
    },
    grantConsent() {
      this.consentRequired = false;
    },
    declineConsent() { 
      alert('Consent required to proceed with new configuration.'); 
    },
    handleOnboarded(profile) {
      this.onboarded = true;
      this.userProfile = profile;
      this.tasks = profile && profile.tasks ? profile.tasks : [];
      this.hasFullAccess = profile && profile.name === 'Rachelle Friloux';
      this.insight = `Hello ${profile.name}, Sallie is ready!`;
    },
    respondToUser(userName, scenario = null) {
      if (scenario) {
        const map = { stressed: 'anger', overwhelmed: 'sad', celebration: 'joy', soothe: 'calm', motivate: 'focus', clarify: 'focus' };
        this.emotion = map[scenario] || 'calm';
        this.emotionMessage = `Scenario: ${scenario}`;
      }
      this.updateVisuals();
    },
    flagsUpdated(){ 
      this.updateVisuals(); 
    },
    toggleTransparency(){ 
      this.showTransparency = !this.showTransparency; 
    },
    async executeGoal(){
      if(!this.adHocGoal) return;
      console.log('Executing goal:', this.adHocGoal);
      // Simple mock implementation
      this.lastDecision = 'Goal processed';
      this.lastProvenance = ['Mock provenance'];
    }
  }
};
</script>

<style>
#app { font-family:'Inter',sans-serif; min-height:100vh; padding:32px; }
.scenario-bar { display:flex; gap:8px; margin-bottom:16px; flex-wrap:wrap; }
.scenario-bar button { background:#4f46e5; color:#fff; border:none; padding:8px 12px; border-radius:6px; cursor:pointer; font-size:0.95em; margin-bottom:4px; }
@keyframes subtleGradientShift {
  0% { filter:brightness(100%); }
  50% { filter:brightness(105%); }
  100% { filter:brightness(100%); }
}
.dynamic-gradient-active { animation: subtleGradientShift 12s ease-in-out infinite; }
</style>