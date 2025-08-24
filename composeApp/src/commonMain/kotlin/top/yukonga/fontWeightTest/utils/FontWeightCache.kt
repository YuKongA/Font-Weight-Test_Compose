package top.yukonga.fontWeightTest.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.font.FontWeight

@Stable
object FontWeightCache {
    private val weightCache = mutableMapOf<Int, FontWeight>()

    fun getFontWeight(value: Int): FontWeight {
        return weightCache.getOrPut(value) {
            FontWeight(value.coerceIn(1, 1000))
        }
    }
}

@Stable
fun getOptimizedFontWeight(value: Int): FontWeight {
    return commonFontWeights[value] ?: FontWeightCache.getFontWeight(value)
}
