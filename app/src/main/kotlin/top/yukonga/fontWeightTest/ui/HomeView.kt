package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    layoutType: NavigationSuiteType,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    colorMode: Int
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .verticalScroll(scrollState)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CardView {
            Text(text = stringResource(R.string.font_weight))
            AllWeightText()
        }
        if (layoutType != NavigationSuiteType.NavigationBar) {
            LandscapeContent(layoutType, colorMode)
        } else {
            PortraitContent(layoutType, colorMode)
        }
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}

@Composable
fun LandscapeContent(layoutType: NavigationSuiteType, colorMode: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            CardView {
                Text(text = stringResource(R.string.comparison_display))
                ComparisonDisplay(layoutType)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            CardView {
                Text(text = stringResource(R.string.variable_font))
                SliderTestView(colorMode)
            }
        }
    }
}

@Composable
fun PortraitContent(layoutType: NavigationSuiteType, colorMode: Int) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CardView {
            Text(text = stringResource(R.string.comparison_display))
            ComparisonDisplay(layoutType)
        }
        CardView {
            Text(text = stringResource(R.string.variable_font))
            SliderTestView(colorMode)
        }
    }
}

@Composable
fun ComparisonDisplay(layoutType: NavigationSuiteType) {
    if (layoutType != NavigationSuiteType.NavigationBar) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DeviceFontTestView(stringResource(R.string.device_font))
            MiSansTestView("MiSans VF:")
        }
    } else {
        DeviceFontTestView(stringResource(R.string.device_font))
        MiSansTestView("MiSans VF:")
    }
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
        fontSize = 14.sp,
        maxLines = 1
    )
}

@Composable
fun MiSansTestView(text: String) {
    Column {
        Text(text = text, fontSize = 15.sp)
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
    Column {
        Text(text = text, fontSize = 15.sp)
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
fun SliderTestView(colorMode: Int) {
    val customText = remember { mutableStateOf("") }
    val fontWeightValue = remember { mutableFloatStateOf(400f) }
    val interactionSource = remember { MutableInteractionSource() }
    val inputText = remember { mutableStateOf(fontWeightValue.floatValue.toInt().toString()) }
    val textWidthDp =
        with(LocalDensity.current) { rememberTextMeasurer().measure(text = "1000", style = MaterialTheme.typography.bodyMedium).size.width.toDp() }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = customText.value.ifEmpty { "永 の A 6" },
            fontSize = 24.sp,
            fontWeight = FontWeight(fontWeightValue.floatValue.toInt())
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                modifier = Modifier.weight(1f),
                value = fontWeightValue.floatValue,
                onValueChange = { newValue ->
                    fontWeightValue.floatValue = newValue
                    inputText.value = newValue.toInt().toString()
                },
                interactionSource = interactionSource,
                valueRange = 1f..999f
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
                    .padding(start = 8.dp)
                    .width(textWidthDp),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontStyle = MaterialTheme.typography.bodyMedium.fontStyle
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                decorationBox = {
                    Column {
                        it()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.5.dp)
                                .background(
                                    when (colorMode) {
                                        1 -> Color.LightGray
                                        2 -> Color.DarkGray
                                        else -> if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
                                    }
                                )
                        )
                    }
                }
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