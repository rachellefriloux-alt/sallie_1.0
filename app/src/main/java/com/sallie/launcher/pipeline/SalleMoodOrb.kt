package com.sallie.launcher.pipeline

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.BasicStroke
import java.awt.RenderingHints
import java.awt.RadialGradientPaint
import java.awt.Color as AwtColor
import java.io.File

val moodColors = mapOf(
    "calm"        to listOf(AwtColor(0xB0, 0xE0, 0xE6), AwtColor(0xD8, 0xBF, 0xD8)),
    "focused"     to listOf(AwtColor(0x00, 0xCE, 0xD1), AwtColor(0x46, 0x82, 0xB4)),
    "energized"   to listOf(AwtColor(0xFF, 0xA5, 0x00), AwtColor(0xFF, 0x45, 0x00)),
    "reflective"  to listOf(AwtColor(0x93, 0x70, 0xDB), AwtColor(0x4B, 0x00, 0x82)),
    "guarded"     to listOf(AwtColor(0x55, 0x6B, 0x2F), AwtColor(0x2F, 0x4F, 0x4F)),
    "celebratory" to listOf(AwtColor(0xFF, 0xD7, 0x00), AwtColor(0xFF, 0x69, 0xB4)),
    "hopeful"     to listOf(AwtColor(0xAD, 0xFF, 0x2F), AwtColor(0x40, 0xE0, 0xD0)),
    "melancholy"  to listOf(AwtColor(0x70, 0x80, 0x90), AwtColor(0x2E, 0x2E, 0x2E)),
    "playful"     to listOf(AwtColor(0xFF, 0xB6, 0xC1), AwtColor(0xFF, 0xE4, 0xB5)),
    "resolute"    to listOf(AwtColor(0x8B, 0x00, 0x00), AwtColor(0x00, 0x00, 0x8B))
)

fun createGlowingMistOrb(
    gradientStart: AwtColor,
    gradientEnd: AwtColor,
    glowStrength: Float = 0.6f,
    mistDensity: Float = 0.35f,
    sizePx: Int = 512
): BufferedImage {
    val image = BufferedImage(sizePx, sizePx, BufferedImage.TYPE_INT_ARGB)
    val g2d = image.createGraphics()
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    val center = sizePx / 2f
    val radius = sizePx / 2.2f
    val gradient = RadialGradientPaint(
        java.awt.Point(center.toInt(), center.toInt()),
        radius,
        floatArrayOf(0f, 1f),
        arrayOf(gradientStart, gradientEnd)
    )
    g2d.paint = gradient
    g2d.fillOval(
        (center - radius).toInt(),
        (center - radius).toInt(),
        (radius * 2).toInt(),
        (radius * 2).toInt()
    )
    repeat(4) { i ->
        val alpha = glowStrength / (i + 1)
        g2d.color = AwtColor(gradientStart.red, gradientStart.green, gradientStart.blue, (alpha * 255).toInt())
        g2d.stroke = BasicStroke((6 + i * 4).toFloat())
        g2d.drawOval(
            (center - radius - i * 4).toInt(),
            (center - radius - i * 4).toInt(),
            (radius * 2 + i * 8).toInt(),
            (radius * 2 + i * 8).toInt()
        )
    }
    val mistParticles = (sizePx * mistDensity).toInt()
    repeat(mistParticles) {
        val px = (Math.random() * sizePx).toInt()
        val py = (Math.random() * sizePx).toInt()
        val alpha = (Math.random() * 0.15f).toFloat()
        g2d.color = AwtColor(255, 255, 255, (alpha * 255).toInt())
        g2d.fillOval(px, py, 4, 4)
    }
    g2d.dispose()
    return image
}

fun saveOrbForMood(projectDir: String, mood: String) {
    val (start, end) = moodColors[mood] ?: moodColors["calm"]!!
    val orb = createGlowingMistOrb(start, end)
    val mipmapBase = File("$projectDir/src/main/res/")
    val densities = listOf(
        "mipmap-mdpi" to 48,
        "mipmap-hdpi" to 72,
        "mipmap-xhdpi" to 96,
        "mipmap-xxhdpi" to 144,
        "mipmap-xxxhdpi" to 192
    )
    densities.forEach { (dpi, size) ->
        val dir = File(mipmapBase, dpi)
        dir.mkdirs()
        val scaled = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB).apply {
            createGraphics().drawImage(orb, 0, 0, size, size, null)
        }
        ImageIO.write(scaled, "png", File(dir, "ic_launcher.png"))
    }
}
