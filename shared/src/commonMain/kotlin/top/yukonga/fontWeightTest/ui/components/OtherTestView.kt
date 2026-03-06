package top.yukonga.fontWeightTest.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import top.yukonga.fontWeightTest.utils.fontWeightsList
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun OtherTestView(fontFamily: FontFamily = FontFamily.Default) {
    val testText = "不以物喜，不以己悲。——范仲淹《岳阳楼记》\n" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n" +
            "abcdefghijklmnopqrstuvwxyz\n" +
            "0123456789,."

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        fontWeightsList.forEachIndexed { index, fontWeight ->
            OtherTestTextItem(
                index = index,
                text = testText,
                fontWeight = fontWeight,
                fontFamily = fontFamily
            )
            if (index < fontWeightsList.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun OtherTestTextItem(
    index: Int,
    text: String,
    fontWeight: androidx.compose.ui.text.font.FontWeight,
    fontFamily: FontFamily
) {
    val label = "- ${(index + 1) * 100} -"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NativeVariableText(
            text = label,
            fontSize = 16f,
            fontWeight = fontWeight.weight,
            color = MiuixTheme.colorScheme.onBackground,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center
        )
        NativeVariableText(
            text = text,
            fontSize = 16f,
            fontWeight = fontWeight.weight,
            color = MiuixTheme.colorScheme.onBackground,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center
        )
        NativeVariableText(
            text = text,
            fontSize = 16f,
            fontWeight = fontWeight.weight,
            color = MiuixTheme.colorScheme.onBackground,
            italic = true,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center
        )
    }
}
