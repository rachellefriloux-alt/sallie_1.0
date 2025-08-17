// Create stub files for the missing dependencies
export function generateEmotionMeterSVG(size, color) {
  return `<svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">
    <circle cx="${size/2}" cy="${size/2}" r="${size/2 - 2}" stroke="${color}" fill="none" stroke-width="2"/>
  </svg>`;
}