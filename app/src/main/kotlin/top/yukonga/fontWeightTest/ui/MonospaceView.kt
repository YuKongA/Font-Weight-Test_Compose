package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import top.yukonga.fontWeightTest.R
import top.yukonga.fontWeightTest.ui.components.CardView
import top.yukonga.fontWeightTest.ui.components.OtherTestView
import top.yukonga.fontWeightTest.ui.components.WeightTextView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonospaceView(
    layoutType: NavigationSuiteType,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .verticalScroll(scrollState)
            .navigationBarsPadding()
    ) {
        if (layoutType != NavigationSuiteType.NavigationBar) {
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                ) {
                    CardView {
                        Text(text = stringResource(R.string.normal_font))
                        WeightTextView(fontFamily = FontFamily.Monospace)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    CardView {
                        Text(text = stringResource(R.string.italic_font))
                        WeightTextView(
                            fontStyle = FontStyle.Italic,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            CardView {
                Text(text = stringResource(R.string.normal_font))
                WeightTextView(
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            CardView {
                Text(text = stringResource(R.string.italic_font))
                WeightTextView(
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        CardView {
            Text(text = stringResource(R.string.more_examples))
            OtherTestView(fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}