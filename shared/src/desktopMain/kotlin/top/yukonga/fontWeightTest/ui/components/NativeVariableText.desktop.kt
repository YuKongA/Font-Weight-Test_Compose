package top.yukonga.fontWeightTest.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Text

@Composable
actual fun NativeVariableText(
    text: String,
    fontSize: Float,
    fontWeight: Int,
    color: Color,
    italic: Boolean,
    fontFamily: FontFamily,
    textAlign: TextAlign,
    maxLines: Int,
    modifier: Modifier
) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        fontWeight = FontWeight(fontWeight.coerceIn(1, 1000)),
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        fontFamily = fontFamily,
        textAlign = textAlign,
        color = color,
        maxLines = maxLines,
        modifier = modifier
    )
}
