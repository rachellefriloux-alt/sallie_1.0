// verificationEngine.js
// Verifies onboarding steps and user choices for Sallie

export default {
  verifyStep(step, data) {
    const complete = Boolean(step) && data != null && (Array.isArray(data) ? data.length > 0 : true);
    return complete ? 'Verified' : 'Incomplete';
  },
  verifyConsent(consent) {
    return consent === true ? 'Consent confirmed' : 'Consent required';
  }
};
