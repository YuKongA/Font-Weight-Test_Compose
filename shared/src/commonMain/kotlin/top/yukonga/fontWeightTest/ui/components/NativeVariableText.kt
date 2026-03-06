package top.yukonga.fontWeightTest.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign

@Composable
expect fun NativeVariableText(
    text: String,
    fontSize: Float,
    fontWeight: Int,
    color: Color,
    italic: Boolean = false,
    fontFamily: FontFamily = FontFamily.Default,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
)
