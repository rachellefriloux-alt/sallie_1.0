// personaSwitcher.js
// Handles persona switching logic for Sallie

export default {
  currentPersona: 'Just Me',
  switchPersona(newPersona) {
    this.currentPersona = newPersona;
    // TODO: Integrate with mood signal and UI
    return `Persona switched to ${newPersona}`;
  },
  getCurrentPersona() {
    return this.currentPersona;
  }
};
