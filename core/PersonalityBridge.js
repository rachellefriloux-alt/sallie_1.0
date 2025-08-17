// Personality bridge
let traits = {};

export function getTraits() {
  return traits;
}

export function onTraits(callback) {
  // In a real implementation, this would set up a listener
  callback();
}