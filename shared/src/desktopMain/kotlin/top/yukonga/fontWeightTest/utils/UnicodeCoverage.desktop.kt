package top.yukonga.fontWeightTest.utils

import java.awt.GraphicsEnvironment

actual object UnicodeGlyphSupport {
    private val allFonts by lazy {
        GraphicsEnvironment.getLocalGraphicsEnvironment().allFonts
    }

    actual fun hasGlyph(codePoint: Int): Boolean {
        return allFonts.any { it.canDisplay(codePoint) }
    }

    actual fun hasGlyphs(codePoints: IntArray): BooleanArray {
        val fonts = allFonts
        return BooleanArray(codePoints.size) { i ->
            fonts.any { it.canDisplay(codePoints[i]) }
        }
    }
}
