package top.yukonga.fontWeightTest.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.fontWeightTest.utils.fontWeightList
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun OtherTestView(fontFamily: FontFamily? = null) {
    OtherTestText(
        "不以物喜，不以己悲。——范仲淹《岳阳楼记》\n" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "0123456789,.",
        fontFamily
    )
}

@Composable
fun OtherTestText(text: String, fontFamily: FontFamily?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        fontWeightList.forEachIndexed { index, fontWeight ->
            Text(
                text = "- ${(index + 1) * 100} - \n$text",
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
            Text(
                text = text,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
            if (index < fontWeightList.size - 1) {
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    }
}