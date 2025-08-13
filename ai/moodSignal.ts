
// moodSignal.ts
// Real-time emotional signal parsing from phrasing, pacing, and context
// Optional: NLP hooks, memory fallback, override triggers, intent routing

export type MoodSignal = {
  tone: string;
  pacing: string;
  context: string;
  persona: string;
  override?: string;
  urgency?: string;
  validation?: string;
};

import { analyzeSentiment, extractKeywords } from './nlpEngine';

export function parseMoodSignal(input: string, context: any, persona: string): MoodSignal {

  const sentiment = analyzeSentiment(input);
  const keywords = extractKeywords(input);
    let tone = 'calm';
    let pacing = 'steady';
    let urgency = 'normal';
    let validation = 'gentle';
    if (sentiment === 'negative' || context.stressLevel > 7) {
      tone = 'sad';
    }
    if (sentiment === 'positive' || persona === 'optimist' || keywords.includes('happy')) {
      tone = 'joy';
    }
    if (keywords.includes('angry') || sentiment === 'angry') {
      tone = 'anger';
    }
  if (input.match(/urgent|now|asap/i)) urgency = 'high';
  if (persona === 'Realist') {
    tone = 'direct';
    pacing = 'brisk';
    validation = 'clear';
  }
  return {
    tone,
    pacing,
    context: context || 'default',
    persona,
    urgency,
    validation
  };
}

export function triggerOverride(signal: MoodSignal, override: string): MoodSignal {
  return { ...signal, override };
}

export function routeIntent(signal: MoodSignal): string {
  // Optional enhancement: route based on urgency/persona
  if (signal.urgency === 'high') return 'priorityQueue';
  if (signal.persona === 'Dreamer') return 'creativeFlow';
  return 'defaultRoute';
}
