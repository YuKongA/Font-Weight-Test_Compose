package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.fontWeightTest.R
import top.yukonga.fontWeightTest.ui.components.CardView
import top.yukonga.fontWeightTest.ui.components.OtherTestView
import top.yukonga.fontWeightTest.ui.components.WeightTextView

@Preview
@Composable
fun SerifView(height: Dp = 0.dp) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        CardView {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.normal_font),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            WeightTextView(fontFamily = FontFamily.Serif)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        CardView {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.italic_font),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            WeightTextView(
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        CardView {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.more_examples),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OtherTestView(FontFamily.Serif)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(height + 20.dp))
    }
}