package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.fontWeightTest.R
import top.yukonga.fontWeightTest.misc.fontWeightList
import top.yukonga.fontWeightTest.misc.miSansList
import top.yukonga.fontWeightTest.ui.components.CardView

@Preview
@Composable
fun HomeView() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        CardView {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.font_weight),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            AllWeightText()
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        CardView {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.comparison_display),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            DeviceFontTestView(stringResource(R.string.device_font))
            Spacer(modifier = Modifier.height(4.dp))
            MiSansTestView("MiSans VF:")
        }
        Spacer(modifier = Modifier.height(20.dp))
        CardView {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.variable_font),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            SeekbarTestView()
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AllWeightText() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        WeightText("100 - 淡体 Thin (Hairline)", FontWeight.Thin)
        WeightText("200 - 特细 ExtraLight (UltraLight)", FontWeight.ExtraLight)
        WeightText("300 - 细体 Light", FontWeight.Light)
        WeightText("400 - 标准 Normal (Regular)", FontWeight.Normal)
        WeightText("500 - 适中 Medium", FontWeight.Medium)
        WeightText("600 - 次粗 SemiBold (DemiBold)", FontWeight.SemiBold)
        WeightText("700 - 粗体 Bold", FontWeight.Bold)
        WeightText("800 - 特粗 ExtraBold (UltraBold)", FontWeight.ExtraBold)
        WeightText("900 - 浓体 Black (Heavy)", FontWeight.Black)
    }
}

@Composable
fun WeightText(description: String, fontWeight: FontWeight) {
    Text(
        text = description,
        fontWeight = fontWeight,
        fontSize = 14.sp,
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
        Text(
            text = text,
            fontSize = 15.sp
        )
        MiSansTest("永")
        MiSansTest("の")
        MiSansTest("A")
        MiSansTest("6")
    }
}

@Composable
fun MiSansTest(text: String) {
    Row {
        fontWeightList.forEachIndexed { index, fontWeight ->
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = fontWeight,
                fontFamily = FontFamily(Font(miSansList[index], weight = fontWeight))
            )
        }
    }
}

@Composable
fun DeviceFontTestView(text: String) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = 15.sp
        )
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
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SeekbarTestView() {
    val customText = remember { mutableStateOf("") }
    val fontWeightValue = remember { mutableFloatStateOf(400f) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (customText.value != "") customText.value else "永 の A 6",
            fontSize = 24.sp,
            fontWeight = FontWeight(fontWeightValue.floatValue.toInt())
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = fontWeightValue.floatValue,
                onValueChange = { newValue -> fontWeightValue.floatValue = newValue },
                interactionSource = interactionSource,
                valueRange = 1f..999f,
                modifier = Modifier.weight(1f)

            )
            Text(
                text = fontWeightValue.floatValue.toInt().toString(),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .sizeIn(minWidth = 32.dp),
                textAlign = TextAlign.End,
                fontSize = 16.sp,
            )
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            value = customText.value,
            onValueChange = { customText.value = it },
            label = { Text(stringResource(R.string.custom_text)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
    }
}