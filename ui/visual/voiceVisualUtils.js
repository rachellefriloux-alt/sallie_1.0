// Voice visualization utilities
export function generateWaveformSVG(levels, color) {
  const width = 200;
  const height = 40;
  const barWidth = width / levels.length;
  const maxLevel = Math.max(...levels);
  
  let path = '';
  levels.forEach((level, i) => {
    const barHeight = (level / maxLevel) * height;
    const x = i * barWidth;
    const y = height - barHeight;
    path += `<rect x="${x}" y="${y}" width="${barWidth-1}" height="${barHeight}" fill="${color}"/>`;
  });
  
  return `<svg width="${width}" height="${height}" viewBox="0 0 ${width} ${height}">
    ${path}
  </svg>`;
}