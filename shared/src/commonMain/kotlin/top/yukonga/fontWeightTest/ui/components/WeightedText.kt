package top.yukonga.fontWeightTest.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import top.yukonga.fontWeightTest.utils.fontWeightsList
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun WeightTextView(
    fontStyle: FontStyle = FontStyle.Normal,
    fontFamily: FontFamily = FontFamily.Default
) {
    val text = "伤仲永 にほんご 한국어 AaBbCc 123"
    val labels = List(fontWeightsList.size) { index ->
        "${(index + 1) * 100} - $text"
    }

    fontWeightsList.forEachIndexed { index, fontWeight ->
        NativeVariableText(
            text = labels[index],
            fontSize = 15.4f,
            fontWeight = fontWeight.weight,
            color = MiuixTheme.colorScheme.onBackground,
            italic = fontStyle == FontStyle.Italic,
            fontFamily = fontFamily,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
