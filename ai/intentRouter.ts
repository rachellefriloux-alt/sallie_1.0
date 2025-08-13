// intentRouter.ts
// Routes requests based on emotional urgency, strategic priority, or persona mode

export type IntentRoute = 'priorityQueue' | 'creativeFlow' | 'defaultRoute' | 'fallbackRoute';

export function routeIntent(signal: any): IntentRoute {
  if (signal.urgency === 'high') return 'priorityQueue';
  if (signal.persona === 'Dreamer') return 'creativeFlow';
  if (signal.override) return 'fallbackRoute';
  return 'defaultRoute';
}
