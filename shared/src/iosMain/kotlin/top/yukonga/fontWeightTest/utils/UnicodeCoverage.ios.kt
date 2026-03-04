package top.yukonga.fontWeightTest.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.interpretObjCPointer
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreText.CTFontCopyCharacterSet
import platform.CoreText.CTFontCreateWithName
import platform.Foundation.NSMutableCharacterSet
import platform.UIKit.UIFont

@OptIn(ExperimentalForeignApi::class)
actual object UnicodeGlyphSupport {
    private val combinedCharacterSet by lazy {
        val mutableSet = NSMutableCharacterSet()

        @Suppress("UNCHECKED_CAST")
        val families = UIFont.familyNames
        families.forEach { familyName ->
            val family = familyName?.toString() ?: return@forEach

            @Suppress("UNCHECKED_CAST")
            val fontNames = UIFont.fontNamesForFamilyName(family)
            fontNames.forEach fontLoop@{ fontName ->
                val name = fontName?.toString() ?: return@fontLoop
                val cfName = CFStringCreateWithCString(null, name, kCFStringEncodingUTF8)
                    ?: return@fontLoop
                val ctFont = CTFontCreateWithName(cfName, 14.0, null)
                CFRelease(cfName)
                ctFont ?: return@fontLoop
                val charSet = CTFontCopyCharacterSet(ctFont)
                CFRelease(ctFont)
                charSet ?: return@fontLoop
                mutableSet.formUnionWithCharacterSet(
                    interpretObjCPointer(charSet.rawValue)
                )
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
