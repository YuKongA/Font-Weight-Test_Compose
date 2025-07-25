package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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

@Composable
fun SansSerifView(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .overScrollVertical()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp + padding.calculateTopPadding()))
            CardView {
                WeightTextView()
            }
            SmallTitle(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(Res.string.italic_font),
            )
            CardView {
                WeightTextView(fontStyle = FontStyle.Italic)
            }
            SmallTitle(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(Res.string.more_examples),
            )
        }
        item {
            CardView {
                OtherTestView()
            }
            Spacer(Modifier.height(padding.calculateBottomPadding() + 12.dp))
        }
    }
}