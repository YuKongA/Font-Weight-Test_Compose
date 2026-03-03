package top.yukonga.fontWeightTest.utils

import android.graphics.Paint

actual object UnicodeGlyphSupport {
    private val paint = Paint()

    actual fun hasGlyph(codePoint: Int): Boolean {
        return paint.hasGlyph(String(Character.toChars(codePoint)))
    }
}
