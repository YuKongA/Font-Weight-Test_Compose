package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import fontweighttest.shared.generated.resources.Res
import fontweighttest.shared.generated.resources.italic_font
import fontweighttest.shared.generated.resources.monospace
import fontweighttest.shared.generated.resources.more_examples
import org.jetbrains.compose.resources.stringResource
import top.yukonga.fontWeightTest.ui.components.CardView
import top.yukonga.fontWeightTest.ui.components.OtherTestView
import top.yukonga.fontWeightTest.ui.components.PageScaffold
import top.yukonga.fontWeightTest.ui.components.WeightTextView
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle

@Composable
fun MonospacePage(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {
    PageScaffold(
        title = stringResource(Res.string.monospace),
        topAppBarScrollBehavior = topAppBarScrollBehavior,
        padding = padding
    ) {
        item(key = "normal_font") {
            CardView {
                WeightTextView(fontFamily = FontFamily.Monospace)
            }
        }

        item(key = "italic_title") {
            SmallTitle(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(Res.string.italic_font),
            )
        }

        item(key = "italic_font") {
            CardView {
                WeightTextView(
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        item(key = "examples_title") {
            SmallTitle(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(Res.string.more_examples),
            )
        }

        item(key = "examples") {
            CardView {
                OtherTestView(fontFamily = FontFamily.Monospace)
            }
        }
    }
}
