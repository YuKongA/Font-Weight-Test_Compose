package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import fontweighttest.composeapp.generated.resources.Res
import fontweighttest.composeapp.generated.resources.italic_font
import fontweighttest.composeapp.generated.resources.more_examples
import org.jetbrains.compose.resources.stringResource
import top.yukonga.fontWeightTest.ui.components.CardView
import top.yukonga.fontWeightTest.ui.components.OtherTestView
import top.yukonga.fontWeightTest.ui.components.WeightTextView
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun MonospaceView(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {
    val layoutDirection = LocalLayoutDirection.current
    LazyColumn(
        modifier = Modifier
            .scrollEndHaptic()
            .overScrollVertical()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(
            top = padding.calculateTopPadding() + 12.dp,
            start = padding.calculateStartPadding(layoutDirection),
            end = padding.calculateEndPadding(layoutDirection),
            bottom = padding.calculateBottomPadding() + 12.dp
        ),
    ) {
        item {
            CardView {
                WeightTextView(
                    fontFamily = FontFamily.Monospace
                )
            }
            SmallTitle(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(Res.string.italic_font),
            )
            CardView {
                WeightTextView(
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Monospace
                )
            }
            SmallTitle(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(Res.string.more_examples),
            )
        }
        item {
            CardView {
                OtherTestView(
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}