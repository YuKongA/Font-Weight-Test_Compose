package top.yukonga.fontWeightTest.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.font.FontWeight

@Stable
data class FontDisplayState(
    val customText: String = "",
    val fontSizeValue: Int = 24,
    val fontWeightValue: Int = 400
) {
    val effectiveFontSize = fontSizeValue.coerceIn(1, 96)
    val effectiveFontWeight = getOptimizedFontWeight(fontWeightValue.coerceIn(1, 999))
    val displayText = customText.ifEmpty { "永 の 한 A 6" }
}

@Stable
fun getOptimizedFontWeight(value: Int): FontWeight {
    return commonFontWeights[value] ?: FontWeight(value.coerceIn(1, 1000))
}

@Stable
val fontWeightsList = listOf(
    FontWeight.Thin, // W100
    FontWeight.ExtraLight, // W200
    FontWeight.Light, // W300
    FontWeight.Normal, // W400
    FontWeight.Medium, // W500
    FontWeight.SemiBold, // W600
    FontWeight.Bold, // W700
    FontWeight.ExtraBold, // W800
    FontWeight.Black // W900
)

@Stable
val commonFontWeights = mapOf(
    100 to FontWeight.Thin,
    200 to FontWeight.ExtraLight,
    300 to FontWeight.Light,
    400 to FontWeight.Normal,
    500 to FontWeight.Medium,
    600 to FontWeight.SemiBold,
    700 to FontWeight.Bold,
    800 to FontWeight.ExtraBold,
    900 to FontWeight.Black
)

@Stable
val fontWeightDescriptions = listOf(
    "淡体 Thin (Hairline)", // W100
    "特细 ExtraLight (UltraLight)", // W200
    "细体 Light", // W300
    "标准 Normal (Regular)", // W400
    "适中 Medium", // W500
    "次粗 SemiBold (DemiBold)", // W600
    "粗体 Bold", // W700
    "特粗 ExtraBold (UltraBold)", // W800
    "浓体 Black (Heavy)" // W900
)

@Stable
val testCharacters = listOf("永", "の", "한", "A", "6")

@Stable
fun parseUnicodeNotationToText(input: String): String {
    val regex = Regex("""U\+([0-9a-fA-F]{1,6})""")
    return regex.replace(input) { match ->
        val hex = match.groupValues[1]
        val cp = hex.toIntOrNull(16) ?: return@replace match.value
        if (cp in 0..0x10FFFF && (cp !in 0xD800..0xDFFF)) {
            if (cp <= 0xFFFF) {
                cp.toChar().toString()
            } else {
                val u = cp - 0x10000
                val high = 0xD800 + (u shr 10)
                val low = 0xDC00 + (u and 0x3FF)
                charArrayOf(high.toChar(), low.toChar()).concatToString()
            }
        } else {
            match.value
        }
    }
}
