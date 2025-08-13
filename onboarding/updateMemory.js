// updateMemory.js
// Updates Sallie's memory with onboarding and user profile data

export default {
  updateProfile(profile) {
  // Update Sallie's memory with new data
  SallieMemory.update(memoryData)
    return `Profile updated for ${profile.name}`;
  },
  updateStep(step, status) {
  // Update onboarding step status
  OnboardingStatus.setStepStatus(stepId, status)
    return `Step '${step}' marked as ${status}`;
  }
};
