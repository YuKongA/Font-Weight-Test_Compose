package top.yukonga.fontWeightTest.utils

import android.graphics.Paint

actual object UnicodeGlyphSupport {
    private val paint = Paint()

    actual fun hasGlyph(codePoint: Int): Boolean {
        return paint.hasGlyph(String(Character.toChars(codePoint)))
    }

    actual fun hasGlyphs(codePoints: IntArray): BooleanArray {
        val results = BooleanArray(codePoints.size)
        for (i in codePoints.indices) {
            results[i] = hasGlyph(codePoints[i])
        }
        return results
    }
}
