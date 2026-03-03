package top.yukonga.fontWeightTest.utils

import androidx.compose.runtime.Stable

@Stable
fun convertCodePointToString(cp: Int): String {
    if (cp in 0..0x10FFFF && (cp !in 0xD800..0xDFFF)) {
        return if (cp <= 0xFFFF) {
            cp.toChar().toString()
        } else {
            val u = cp - 0x10000
            val high = 0xD800 + (u shr 10)
            val low = 0xDC00 + (u and 0x3FF)
            charArrayOf(high.toChar(), low.toChar()).concatToString()
        }
    }
    return ""
}

@Stable
fun parseUnicodeNotationToText(input: String): String {
    val regex = Regex("""U\+([0-9a-fA-F]{1,6})""")
    return regex.replace(input) { match ->
        val hex = match.groupValues[1]
        val cp = hex.toIntOrNull(16) ?: return@replace match.value
        convertCodePointToString(cp).ifEmpty { match.value }
    }
}

fun String.codePointCount(): Int {
    var count = 0
    var i = 0
    while (i < length) {
        val c = this[i]
        if (c.isHighSurrogate() && i + 1 < length && this[i + 1].isLowSurrogate()) {
            i += 2
        } else {
            i++
        }
        count++
    }
    return count
}

fun String.takeCodePoints(n: Int): String {
    var count = 0
    var i = 0
    while (i < length && count < n) {
        val c = this[i]
        if (c.isHighSurrogate() && i + 1 < length && this[i + 1].isLowSurrogate()) {
            i += 2
        } else {
            i++
        }
        count++
    }
    return substring(0, i)
}
