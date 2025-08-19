// Visual theme generator
export function generateTheme(mood) {
  const themes = {
    calm: { text: '#333', background: '#f7f7fa', accent: '#80deea', gradient: 'linear-gradient(135deg, #f7f7fa, #e8f5e8)' },
    focus: { text: '#222', background: '#f0f8ff', accent: '#4682b4', gradient: 'linear-gradient(135deg, #f0f8ff, #e0f0ff)' },
    happy: { text: '#333', background: '#fffacd', accent: '#ffd700', gradient: 'linear-gradient(135deg, #fffacd, #fff8dc)' }
  };
  return themes[mood] || themes.calm;
}