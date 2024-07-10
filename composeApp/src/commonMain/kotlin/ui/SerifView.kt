package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fontweighttest.composeapp.generated.resources.Res
import fontweighttest.composeapp.generated.resources.italic_font
import fontweighttest.composeapp.generated.resources.more_examples
import fontweighttest.composeapp.generated.resources.normal_font
import org.jetbrains.compose.resources.stringResource
import ui.components.CardView
import ui.components.OtherTestView
import ui.components.WeightTextView

@Composable
fun SerifView() {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.padding(horizontal = 20.dp).verticalScroll(scrollState)) {
        CardView {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.normal_font),
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
                text = stringResource(Res.string.italic_font),
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
                text = stringResource(Res.string.more_examples),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OtherTestView(FontFamily.Serif)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

