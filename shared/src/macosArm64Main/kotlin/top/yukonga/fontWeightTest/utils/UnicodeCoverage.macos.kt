package top.yukonga.fontWeightTest.utils

import platform.AppKit.NSFont

actual object UnicodeGlyphSupport {
    private val fallbackFonts = listOfNotNull(
        NSFont.systemFontOfSize(14.0),
        NSFont.fontWithName("PingFang SC", 14.0),
        NSFont.fontWithName("PingFang TC", 14.0),
        NSFont.fontWithName("PingFang HK", 14.0),
        NSFont.fontWithName("Hiragino Sans", 14.0),
        NSFont.fontWithName("Hiragino Mincho ProN", 14.0),
        NSFont.fontWithName("Songti SC", 14.0),
        NSFont.fontWithName("Heiti SC", 14.0),
        NSFont.fontWithName("Arial Unicode MS", 14.0)
    )
    private val characterSets = fallbackFonts.map { it.coveredCharacterSet }

    actual fun hasGlyph(codePoint: Int): Boolean {
        val cp = codePoint.toUInt()
        return characterSets.any { it.longCharacterIsMember(cp) }
    }
}
