package top.yukonga.fontWeightTest.utils

actual object UnicodeGlyphSupport {
    actual fun hasGlyph(codePoint: Int): Boolean {
        // iOS implementation currently does not perform real glyph detection.
        // Return a conservative value to avoid runtime crashes and effectively
        // disable Unicode coverage on iOS.
        return false
    }

    actual fun hasGlyphs(codePoints: IntArray): BooleanArray {
        // Evaluate each code point using the single-glyph check.
        return BooleanArray(codePoints.size) { index ->
            hasGlyph(codePoints[index])
        }
    }
}
