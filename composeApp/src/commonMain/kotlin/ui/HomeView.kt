package ui

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fontweighttest.composeapp.generated.resources.MiSansVF
import fontweighttest.composeapp.generated.resources.Res
import fontweighttest.composeapp.generated.resources.comparison_display
import fontweighttest.composeapp.generated.resources.device_font
import fontweighttest.composeapp.generated.resources.font_weight
import misc.fontWeightList
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import ui.components.CardView

@Composable
fun HomeView() {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        Text(
            text = stringResource(Res.string.font_weight),
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 16.sp
        )
        CardView {
            AllWeightText()
        }
        Text(
            text = stringResource(Res.string.comparison_display),
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            fontSize = 16.sp
        )
        CardView {
            DeviceFontTestView(stringResource(Res.string.device_font))
            Spacer(modifier = Modifier.padding(top = 8.dp))
            MiSansTestView("MiSans VF:")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AllWeightText() {
    Column(modifier = Modifier.padding(16.dp)) {
        WeightText("100 - 淡体 Thin (Hairline)", FontWeight.Thin)
        WeightText("200 - 特细 Extra Light (Ultra Light)", FontWeight.ExtraLight)
        WeightText("300 - 细体 Light", FontWeight.Light)
        WeightText("400 - 标准 Normal (Regular)", FontWeight.Normal)
        WeightText("500 - 适中 Medium", FontWeight.Medium)
        WeightText("600 - 次粗 Semi Bold (Demi Bold)", FontWeight.SemiBold)
        WeightText("700 - 粗体 Bold", FontWeight.Bold)
        WeightText("800 - 特粗 Extra Bold (Ultra Bold)", FontWeight.ExtraBold)
        WeightText("900 - 浓体 Black (Heavy)", FontWeight.Black)
    }
}

@Composable
fun WeightText(description: String, fontWeight: FontWeight) {
    Text(
        text = description,
        fontWeight = fontWeight,
        fontSize = 16.sp,
        maxLines = 1
    )
}

@Composable
fun MiSansTestView(text: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Text(text = text)
        MiSansTest("永")
        MiSansTest("の")
        MiSansTest("A")
        MiSansTest("6")
    }
}

@Composable
fun MiSansTest(text: String) {
    Row {
        fontWeightList.forEach { fontWeight ->
            Text(
                text = text,
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(Res.font.MiSansVF, weight = fontWeight))
            )
        }
    }
}

@Composable
fun DeviceFontTestView(text: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        Text(text = text)
        MoreTestText("永")
        MoreTestText("の")
        MoreTestText("A")
        MoreTestText("6")
    }
}

@Composable
fun MoreTestText(text: String) {
    Row {
        fontWeightList.forEach { fontWeight ->
            Text(
                text = text,
                fontWeight = fontWeight,
                fontSize = 22.sp
            )
        }
    }
}
