package top.yukonga.fontWeightTest.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.fontWeightTest.R
import top.yukonga.fontWeightTest.ui.components.CardView
import top.yukonga.fontWeightTest.ui.components.OtherTestView
import top.yukonga.fontWeightTest.ui.components.WeightTextView

@Preview
@Composable
fun SansSerifView(height: Dp = 0.dp) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        val orientation = LocalConfiguration.current.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                ) {
                    CardView {
                        Text(text = stringResource(R.string.normal_font))
                        WeightTextView()
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    CardView {
                        Text(text = stringResource(R.string.italic_font))
                        WeightTextView(fontStyle = FontStyle.Italic)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            CardView {
                Text(text = stringResource(R.string.normal_font))
                WeightTextView()
            }
            Spacer(modifier = Modifier.height(20.dp))
            CardView {
                Text(text = stringResource(R.string.italic_font))
                WeightTextView(fontStyle = FontStyle.Italic)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        CardView {
            Text(text = stringResource(R.string.more_examples))
            OtherTestView()
        }
        Spacer(modifier = Modifier.height(height + 20.dp))
    }
}