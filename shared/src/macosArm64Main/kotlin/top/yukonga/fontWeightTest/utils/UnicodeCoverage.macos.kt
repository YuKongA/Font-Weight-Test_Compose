package top.yukonga.fontWeightTest.utils

import platform.AppKit.NSFont
import platform.AppKit.NSFontManager
import platform.Foundation.NSMutableCharacterSet

actual object UnicodeGlyphSupport {
    private val combinedCharacterSet by lazy {
        val mutableSet = NSMutableCharacterSet()

        @Suppress("UNCHECKED_CAST")
        val fontNames = NSFontManager.sharedFontManager.availableFonts
        fontNames.forEach { name ->
            val fontName = name?.toString() ?: return@forEach
            NSFont.fontWithName(fontName, 14.0)?.coveredCharacterSet?.let {
                mutableSet.formUnionWithCharacterSet(it)
            }
        }
        mutableSet
    }

    actual fun hasGlyph(codePoint: Int): Boolean =
        combinedCharacterSet.longCharacterIsMember(codePoint.toUInt())

    actual fun hasGlyphs(codePoints: IntArray): BooleanArray {
        val set = combinedCharacterSet
        return BooleanArray(codePoints.size) { i ->
            set.longCharacterIsMember(codePoints[i].toUInt())
        }
    }
}
