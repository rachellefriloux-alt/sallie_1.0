// Feature flags
export function isEnabled(flag) {
  const flags = {
    auto_contrast: true,
    exp_new_waveform: false,
    dynamic_gradient: true
  };
  return flags[flag] || false;
}

export function experimentalBlocked(flag) {
  return !isEnabled(flag);
}