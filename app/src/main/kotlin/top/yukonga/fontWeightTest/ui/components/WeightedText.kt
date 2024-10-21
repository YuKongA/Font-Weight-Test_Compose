package top.yukonga.fontWeightTest.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import top.yukonga.fontWeightTest.utils.fontWeightList
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun WeightTextView(fontStyle: FontStyle = FontStyle.Normal, fontFamily: FontFamily? = null) {
    Column {
        val text = "伤仲永 にほんご 한국어 ABCD abcd 0123"
        fontWeightList.forEachIndexed { index, fontWeight ->
            Text(
                text = "${(index + 1) * 100} - $text",
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                fontStyle = fontStyle,
                fontSize = 15.4.sp,
                maxLines = 1
            )
        }
    }
}