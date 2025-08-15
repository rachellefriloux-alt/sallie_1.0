// Advanced sentiment analysis and keyword extraction utilities for Sallie
// Future: Integrate with external AI/NLP APIs

export function analyzeSentiment(text: string): string {
  // Future: Use ML/NLP models for deeper analysis
  const negativeWords = ['sad', 'angry', 'upset', 'worried', 'frustrated', 'overwhelmed', 'stressed', 'hurt', 'fear', 'anxious', 'lonely'];
  const positiveWords = ['happy', 'joy', 'excited', 'celebrate', 'win', 'optimist', 'love', 'peace', 'grateful', 'proud', 'hopeful'];
  const lowerText = text.toLowerCase();
  if (negativeWords.some(word => lowerText.includes(word))) return 'negative';
  if (positiveWords.some(word => lowerText.includes(word))) return 'positive';
  if (lowerText.includes('angry')) return 'angry';
  if (lowerText.includes('calm')) return 'calm';
  return 'neutral';
}

export function extractKeywords(text: string): string[] {
  // Future: Use NLP for entity and intent extraction
  const scenarioWords = [
    'sad', 'happy', 'angry', 'support', 'help', 'conflict', 'celebrate', 'win', 'struggle', 'joy', 'excited', 'overwhelmed', 'focused', 'creative', 'reflective', 'repair', 'choose', 'motivate', 'soothe', 'clarify',
    'growth', 'rest', 'connect', 'learn', 'reset', 'plan', 'review', 'inspire', 'protect', 'assert', 'heal', 'explore', 'build', 'share', 'listen', 'lead', 'follow', 'love', 'peace', 'grateful', 'proud', 'hopeful'
  ];
  const words = text.toLowerCase().split(/\W+/);
  return words.filter(word => scenarioWords.includes(word));
}
