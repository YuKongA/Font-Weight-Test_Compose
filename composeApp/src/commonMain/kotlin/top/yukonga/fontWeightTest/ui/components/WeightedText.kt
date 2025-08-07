package top.yukonga.fontWeightTest.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import top.yukonga.fontWeightTest.utils.fontWeightList
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun WeightTextView(
    fontStyle: FontStyle = FontStyle.Normal,
    fontFamily: FontFamily = FontFamily.Default
) {
    val text = remember { "伤仲永 にほんご 한국어 AaBbCc 123" }

    Column {
        fontWeightList.forEachIndexed { index, fontWeight ->
            val label = remember(index, text) { "${(index + 1) * 100} - $text" }
            WeightTextItem(
                text = label,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                fontStyle = fontStyle
            )
        }
    }
}

@Composable
private fun WeightTextItem(
    text: String,
    fontWeight: androidx.compose.ui.text.font.FontWeight,
    fontFamily: FontFamily,
    fontStyle: FontStyle
) {
    Text(
        text = text,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        fontStyle = fontStyle,
        fontSize = 15.4.sp,
        maxLines = 1
    )
}
