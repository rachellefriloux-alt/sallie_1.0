/*
 * Sallie 1.0 Module
 * Persona: Tough love meets soul care.
 * Function: Adaptive persona engine for dynamic personality adjustment.
 * Got it, love.
 */

import { emotionToPersonality, getEmotionState, type EmotionState } from '../ai/utils/emotionMap';

interface PersonaState {
  mode: 'tough_love' | 'soul_care' | 'wise_sister' | 'balanced';
  intensity: number;
  adaptability: number;
  context: string;
  lastUpdate: Date;
}

interface UserContext {
  stressLevel: number;
  taskComplexity: number;
  timeOfDay: string;
  recentInteractions: string[];
  currentGoals: string[];
  mood: string;
}

class AdaptivePersonaEngine {
  private currentPersona: PersonaState;
  private adaptationHistory: PersonaState[] = [];
  private contextAnalyzer: ContextAnalyzer;

  constructor() {
    this.currentPersona = {
      mode: 'balanced',
      intensity: 50,
      adaptability: 80,
      context: 'Initial balanced state',
      lastUpdate: new Date()
    };
    this.contextAnalyzer = new ContextAnalyzer();
  }

  adapt(emotion: EmotionState, userContext: UserContext): PersonaState {
    // Analyze current situation
    const recommendedMode = this.analyzePersonaNeeded(emotion, userContext);
    const intensityAdjustment = this.calculateIntensityAdjustment(userContext);
    
    // Create new persona state
    const newPersona: PersonaState = {
      mode: recommendedMode,
      intensity: Math.max(10, Math.min(90, intensityAdjustment)),
      adaptability: this.calculateAdaptability(userContext),
      context: this.generatePersonaContext(recommendedMode, emotion, userContext),
      lastUpdate: new Date()
    };

    // Store history for learning
    this.adaptationHistory.push(this.currentPersona);
    this.currentPersona = newPersona;

    // Trigger personality change event
    this.notifyPersonaChange(newPersona);

    return newPersona;
  }

  private analyzePersonaNeeded(emotion: EmotionState, userContext: UserContext): PersonaState['mode'] {
    // High stress situations need tough love or wise sister
    if (userContext.stressLevel > 70) {
      return userContext.taskComplexity > 60 ? 'wise_sister' : 'tough_love';
    }

    // Low energy/motivation needs soul care
    if (emotion.intensity < 30 && emotion.primary === 'calm') {
      return 'soul_care';
    }

    // High complexity tasks need wise sister guidance
    if (userContext.taskComplexity > 80) {
      return 'wise_sister';
    }

    // Protective emotions need tough love approach
    if (emotion.primary === 'protective' || emotion.primary === 'determined') {
      return 'tough_love';
    }

    // Supportive/nurturing situations need soul care
    if (emotion.primary === 'supportive' || emotion.primary === 'joyful') {
      return 'soul_care';
    }

    return 'balanced';
  }

  private calculateIntensityAdjustment(userContext: UserContext): number {
    let baseIntensity = 50;

    // Increase intensity for high stress
    baseIntensity += userContext.stressLevel * 0.3;

    // Adjust based on time of day
    const hour = new Date().getHours();
    if (hour >= 6 && hour <= 9) { // Morning - higher intensity
      baseIntensity += 15;
    } else if (hour >= 22 || hour <= 5) { // Late night - lower intensity
      baseIntensity -= 20;
    }

    // Adjust for task complexity
    baseIntensity += userContext.taskComplexity * 0.2;

    return Math.max(10, Math.min(90, baseIntensity));
  }

  private calculateAdaptability(userContext: UserContext): number {
    // Higher adaptability when user seems open to change
    let adaptability = 80;

    // Reduce adaptability if user seems overwhelmed
    if (userContext.stressLevel > 80) {
      adaptability -= 20;
    }

    // Increase adaptability if user is actively engaging
    if (userContext.recentInteractions.length > 3) {
      adaptability += 10;
    }

    return Math.max(20, Math.min(100, adaptability));
  }

  private generatePersonaContext(mode: PersonaState['mode'], emotion: EmotionState, userContext: UserContext): string {
    const contexts = {
      tough_love: `Direct approach needed. Stress: ${userContext.stressLevel}, Emotion: ${emotion.primary}`,
      soul_care: `Nurturing support required. Providing comfort and encouragement`,
      wise_sister: `Guidance mode. Complexity: ${userContext.taskComplexity}, offering wisdom`,
      balanced: `Balanced approach. Adapting to user's current state: ${emotion.primary}`
    };

    return contexts[mode];
  }

  private notifyPersonaChange(newPersona: PersonaState): void {
    // Emit custom event for persona change
    if (typeof window !== 'undefined') {
      const event = new CustomEvent('sallie-persona-change', {
        detail: newPersona
      });
      window.dispatchEvent(event);
    }
  }

  getCurrentPersona(): PersonaState {
    return this.currentPersona;
  }

  getAdaptationHistory(): PersonaState[] {
    return [...this.adaptationHistory];
  }

  analyzePattern(): string {
    if (this.adaptationHistory.length < 3) {
      return 'Insufficient data for pattern analysis';
    }

    const recentModes = this.adaptationHistory.slice(-5).map(p => p.mode);
    const modeCount = recentModes.reduce((acc, mode) => {
      acc[mode] = (acc[mode] || 0) + 1;
      return acc;
    }, {} as Record<string, number>);

    const dominantMode = Object.entries(modeCount)
      .sort(([,a], [,b]) => b - a)[0][0];

    return `Recent pattern shows preference for ${dominantMode} approach`;
  }

  reset(): void {
    this.currentPersona = {
      mode: 'balanced',
      intensity: 50,
      adaptability: 80,
      context: 'Reset to balanced state',
      lastUpdate: new Date()
    };
    this.adaptationHistory = [];
  }
}

class ContextAnalyzer {
  analyzeUserContext(recentMessages: string[], currentTasks: string[]): UserContext {
    return {
      stressLevel: this.calculateStressLevel(recentMessages),
      taskComplexity: this.calculateTaskComplexity(currentTasks),
      timeOfDay: this.getTimeOfDay(),
      recentInteractions: recentMessages.slice(-5),
      currentGoals: currentTasks,
      mood: this.inferMood(recentMessages)
    };
  }

  private calculateStressLevel(messages: string[]): number {
    const stressKeywords = ['urgent', 'stressed', 'overwhelmed', 'panic', 'crisis', 'emergency', 'deadline'];
    const relaxationKeywords = ['calm', 'peaceful', 'relaxed', 'easy', 'simple', 'comfortable'];

    let stressScore = 0;
    const recentMessages = messages.slice(-3).join(' ').toLowerCase();

    stressKeywords.forEach(keyword => {
      if (recentMessages.includes(keyword)) stressScore += 20;
    });

    relaxationKeywords.forEach(keyword => {
      if (recentMessages.includes(keyword)) stressScore -= 15;
    });

    return Math.max(0, Math.min(100, stressScore + 30)); // Base stress level of 30
  }

  private calculateTaskComplexity(tasks: string[]): number {
    const complexityKeywords = ['analyze', 'complex', 'difficult', 'challenging', 'intricate', 'detailed'];
    const simpleKeywords = ['simple', 'easy', 'basic', 'quick', 'straightforward'];

    let complexity = 30; // Base complexity
    const taskText = tasks.join(' ').toLowerCase();

    complexityKeywords.forEach(keyword => {
      if (taskText.includes(keyword)) complexity += 15;
    });

    simpleKeywords.forEach(keyword => {
      if (taskText.includes(keyword)) complexity -= 10;
    });

    return Math.max(0, Math.min(100, complexity));
  }

  private getTimeOfDay(): string {
    const hour = new Date().getHours();
    if (hour >= 5 && hour < 12) return 'morning';
    if (hour >= 12 && hour < 17) return 'afternoon';
    if (hour >= 17 && hour < 21) return 'evening';
    return 'night';
  }

  private inferMood(messages: string[]): string {
    const moodKeywords = {
      positive: ['happy', 'excited', 'great', 'awesome', 'wonderful', 'fantastic'],
      negative: ['sad', 'frustrated', 'angry', 'disappointed', 'worried', 'stressed'],
      neutral: ['okay', 'fine', 'normal', 'regular', 'typical', 'usual']
    };

    const recentText = messages.slice(-2).join(' ').toLowerCase();
    
    for (const [mood, keywords] of Object.entries(moodKeywords)) {
      if (keywords.some(keyword => recentText.includes(keyword))) {
        return mood;
      }
    }

    return 'neutral';
  }
}

export const adaptivePersonaEngine = new AdaptivePersonaEngine();

export { AdaptivePersonaEngine, type PersonaState, type UserContext };