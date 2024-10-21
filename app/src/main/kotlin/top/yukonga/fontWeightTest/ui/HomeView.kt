package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.fontWeightTest.R
import top.yukonga.fontWeightTest.ui.components.CardView
import top.yukonga.fontWeightTest.utils.fontWeightList
import top.yukonga.fontWeightTest.utils.miSansList
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeView(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    ) {
        item {
            Spacer(Modifier.height(12.dp + padding.calculateTopPadding()))
            CardView {
                AllWeightText()
            }
            PortraitContent()
            Spacer(Modifier.height(padding.calculateBottomPadding() + 12.dp))
        }
    }
}

@Composable
fun PortraitContent() {
    SmallTitle(
        text = stringResource(R.string.comparison_display),
        modifier = Modifier.padding(top = 12.dp)
    )
    CardView {
        ComparisonDisplay()
    }
    SmallTitle(
        text = stringResource(R.string.variable_font),
        modifier = Modifier.padding(top = 12.dp)
    )
    CardView {
        SliderTestView()
    }
}

@Composable
fun ComparisonDisplay() {
    DeviceFontTestView(stringResource(R.string.device_font))
    Spacer(Modifier.height(6.dp))
    MiSansTestView("MiSans VF:")
}

@Composable
fun AllWeightText() {
    Column {
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
        maxLines = 1
    )
}

@Composable
fun MiSansTestView(text: String) {
    Column {
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
        fontWeightList.forEachIndexed { index, fontWeight ->
            Text(
                text = text,
                fontWeight = fontWeight,
                fontFamily = FontFamily(Font(miSansList[index], weight = fontWeight))
            )
        }
    }
}

@Composable
fun DeviceFontTestView(text: String) {
    Column {
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
                fontWeight = fontWeight
            )
        }
    }
}

@Composable
fun SliderTestView() {
    val customText = remember { mutableStateOf("") }
    val fontWeightValue = remember { mutableFloatStateOf(400f) }
    val inputText = remember { mutableStateOf(fontWeightValue.floatValue.toInt().toString()) }
    val textWidthDp =
        with(LocalDensity.current) { rememberTextMeasurer().measure(text = "1000", style = MiuixTheme.textStyles.main).size.width.toDp() }
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = customText.value.ifEmpty { "永 の A 6" },
            fontSize = 24.sp,
            fontWeight = FontWeight(fontWeightValue.floatValue.toInt())
        )
        Row {
            Text(
                text = "Font Weight: "
            )
            BasicTextField(
                value = inputText.value,
                onValueChange = { newValue ->
                    val intValue = newValue.toIntOrNull()
                    if (intValue != null && intValue in 1..999) {
                        fontWeightValue.floatValue = intValue.toFloat()
                        inputText.value = newValue
                    } else if (newValue.isEmpty()) {
                        inputText.value = "1"
                    }
                },
                modifier = Modifier
                    .width(textWidthDp),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    color = MiuixTheme.colorScheme.onBackground
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                decorationBox = {
                    Column {
                        it()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.8.dp)
                                .background(if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray)
                        )
                    }
                }
            )
        }
        Slider(
            modifier = Modifier.padding(top = 6.dp),
            progress = fontWeightValue.floatValue,
            onProgressChange = { newValue ->
                fontWeightValue.floatValue = newValue
                inputText.value = newValue.toInt().toString()
            },
            minValue = 1f,
            maxValue = 999f
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            cornerRadius = 14.dp,
            value = customText.value,
            onValueChange = { customText.value = it },
            label = stringResource(R.string.custom_text),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
    }
}