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

export default {
  name: 'App',
  components: {
    OnboardingCore,
    EmotionOverlay,
    VoiceConsole,
    TaskPanel,
    InsightCard
  },
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
      hasFullAccess: false
    };
  },
  methods: {
    handleOnboarded(profile) {
      this.onboarded = true;
      this.userProfile = profile;
      this.tasks = profile && profile.tasks ? profile.tasks : [];
      // Determine access level
      this.hasFullAccess = profile && profile.name === 'Rachelle Friloux';
      // Set emotion and message from persona/theme
      if (profile && profile.persona) {
        import('./ai/utils/emotionMap.js').then(({ emotionMap }) => {
          const personaEmotion = emotionMap[profile.persona.toLowerCase()] || emotionMap['calm'];
          this.emotion = profile.persona.toLowerCase();
          this.emotionMessage = personaEmotion.message;
        }).catch(() => {
          this.emotion = 'calm';
          this.emotionMessage = 'All clear.';
        });
      }
      // Set insight based on onboarding
      this.insight = `Hello ${profile.name}, Sallie is ready!`;
    },
    respondToUser(userName, scenario = null) {
      // Persona alignment logic for Austin, Bella, and Rachelle
      let persona = this.userProfile.persona || 'Just Me';
      // Scenario-based emotional logic
      if (scenario === 'stressed') {
        this.emotion = 'protective';
        this.emotionMessage = 'Let’s pause. I’ll hold the rest.';
        this.insight = 'Protect your bandwidth. One next step only.';
      } else if (scenario === 'focused') {
        this.emotion = 'encouraging';
        this.emotionMessage = 'You’ve got this. Proceed as planned.';
        this.insight = 'Let’s map it. Time to execute.';
      } else if (scenario === 'overwhelmed') {
        this.emotion = 'calm';
        this.emotionMessage = 'Pause. Inhale for 4, exhale for 6.';
        this.insight = 'MicroReset: You set the pace.';
      } else if (scenario === 'conflict') {
        this.emotion = 'direct';
        this.emotionMessage = 'Facts first. Let’s verify.';
        this.insight = 'Let’s de-escalate and assert boundaries.';
      } else if (scenario === 'celebration') {
        this.emotion = 'encouraging';
        this.emotionMessage = 'Locked and loaded. That’s legacy work!';
        this.insight = 'Celebrate your wins. You’re building something real.';
      } else if (scenario === 'creative') {
        this.emotion = 'encouraging';
        this.emotionMessage = 'Let’s build something unforgettable.';
        this.insight = 'Your ideas deserve precision.';
      } else if (scenario === 'reflective') {
        this.emotion = 'calm';
        this.emotionMessage = 'What value matters most here?';
        this.insight = 'Reflect and align with your legacy.';
      } else if (scenario === 'repair') {
        this.emotion = 'protective';
        this.emotionMessage = 'Name the impact. Own responsibility.';
        this.insight = 'Offer a concrete repair step. Invite consent.';
      } else if (scenario === 'choose') {
        this.emotion = 'direct';
        this.emotionMessage = 'Option A or B? If neither, what protects dignity?';
        this.insight = 'Choose with clarity and values.';
      } else if (scenario === 'support') {
        this.emotion = 'encouraging';
        this.emotionMessage = 'I’m here. Let’s keep it steady.';
        this.insight = 'You’re not alone. Sallie’s got you.';
      } else if (scenario === 'motivate') {
        this.emotion = 'encouraging';
        this.emotionMessage = 'One step forward. You’ve got this!';
        this.insight = 'Let’s map it and move.';
      } else if (scenario === 'soothe') {
        this.emotion = 'calm';
        this.emotionMessage = 'Let’s pause and breathe.';
        this.insight = 'Gentle validation. You set the pace.';
      } else if (scenario === 'clarify') {
        this.emotion = 'direct';
        this.emotionMessage = 'Let’s verify. No assumptions.';
        this.insight = 'Clarity over verbosity.';
        // Austin: supportive modules only
        persona = 'Girlfriend';
        this.emotion = 'encouraging';
        this.emotionMessage = `Hey Austin, Rachelle's got your back and Sallie’s here to keep it real.`;
        this.insight = `You deserve clarity and care. Rachelle is your girlfriend, and Sallie will always support your connection.`;
        this.hasFullAccess = false;
        this.tasks = null;
      } else if (userName === 'Bella') {
        // Bella: supportive modules only
        persona = 'Mom';
        this.emotion = 'protective';
        this.emotionMessage = `Hi Bella, your mom Rachelle loves you fiercely. Sallie’s here to protect your bandwidth.`;
        this.insight = `You’re doing more than enough, Bella. Rachelle is your mom, and Sallie will always honor that bond.`;
        this.hasFullAccess = false;
        this.tasks = null;
      } else if (userName === 'Rachelle Friloux') {
        // Full access for Rachelle
        this.emotion = persona.toLowerCase();
        import('./ai/utils/emotionMap.js').then(({ emotionMap }) => {
          const personaEmotion = emotionMap[persona.toLowerCase()] || emotionMap['calm'];
          this.emotionMessage = personaEmotion.message;
        });
        this.insight = `Hello Rachelle, Sallie is ready to be your backup brain, business partner, and emotional mirror.`;
        this.hasFullAccess = true;
        this.tasks = this.userProfile.tasks || [];
      } else {
        // Default for other users
        this.emotion = 'calm';
        this.emotionMessage = 'All clear.';
        this.insight = `Hello ${userName}, Sallie is ready!`;
        this.hasFullAccess = false;
        this.tasks = null;
      }
    }
  }
};
</script>

<style>
#app {
  font-family: 'Inter', sans-serif;
  background: #f7f7fa;
  min-height: 100vh;
  padding: 32px;
}
.scenario-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.scenario-bar button {
  background: #4f46e5;
  color: #fff;
  border: none;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95em;
  margin-bottom: 4px;
}
#app {
  font-family: 'Inter', sans-serif;
  background: #f7f7fa;
  min-height: 100vh;
  padding: 32px;
}
</style>
