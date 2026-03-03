package top.yukonga.fontWeightTest.utils

import java.awt.Font

actual object UnicodeGlyphSupport {
    private val fallbackFont = Font("Dialog", Font.PLAIN, 14)

    actual fun hasGlyph(codePoint: Int): Boolean {
        return fallbackFont.canDisplay(codePoint)
    }
}
