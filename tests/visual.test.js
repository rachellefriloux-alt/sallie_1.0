import { describe, it, expect } from 'vitest'
import { generateTheme } from '../ui/visual/themeGenerator.js'
import { generatePatternSVG } from '../ui/visual/svgGenerator.js'

describe('Visual System', () => {
  it('should generate valid theme for calm mood', () => {
    const theme = generateTheme('calm')
    expect(theme).toHaveProperty('text')
    expect(theme).toHaveProperty('background')
    expect(theme).toHaveProperty('accent')
  })

  it('should generate valid SVG pattern', () => {
    const svg = generatePatternSVG(100, 100, '#ff0000')
    expect(svg).toContain('<svg')
    expect(svg).toContain('</svg>')
    expect(svg).toContain('#ff0000')
  })
})