package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fontweighttest.composeapp.generated.resources.Res
import fontweighttest.composeapp.generated.resources.clear_text
import fontweighttest.composeapp.generated.resources.comparison_display
import fontweighttest.composeapp.generated.resources.custom_text
import fontweighttest.composeapp.generated.resources.device_font
import fontweighttest.composeapp.generated.resources.font_size
import fontweighttest.composeapp.generated.resources.font_weight
import fontweighttest.composeapp.generated.resources.variable_font
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import top.yukonga.fontWeightTest.ui.components.CardView
import top.yukonga.fontWeightTest.utils.fontWeightList
import top.yukonga.fontWeightTest.utils.miSansList
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.utils.overScrollVertical

@Composable
fun HomeView(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {
    val focusManager = LocalFocusManager.current
    LazyColumn(
        modifier = Modifier
            .overScrollVertical()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .clickable(
                indication = null,
                interactionSource = null,
            ) {
                focusManager.clearFocus()
            }
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
        text = stringResource(Res.string.comparison_display),
        modifier = Modifier.padding(top = 6.dp)
    )
    CardView {
        ComparisonDisplay()
    }
    SmallTitle(
        text = stringResource(Res.string.variable_font),
        modifier = Modifier.padding(top = 6.dp)
    )
    CardView {
        SliderTestView()
    }
}

@Composable
fun ComparisonDisplay() {
    DeviceFontTestView(stringResource(Res.string.device_font))
    Spacer(Modifier.height(6.dp))
    MiSansTestView("MiSans VF")
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
    var customText by remember { mutableStateOf("".ifEmpty { "永 の A 6" }) }
    var fontSizeValue by remember { mutableIntStateOf(24) }
    var fontSizeText by remember { mutableStateOf(fontSizeValue.toString()) }
    var fontWeightValue by remember { mutableIntStateOf(400) }
    var fontWeightText by remember { mutableStateOf(fontWeightValue.toString()) }
    val focusManager = LocalFocusManager.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(0.5f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = fontWeightText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty()) {
                        fontWeightValue = 1
                        fontWeightText = ""
                    } else if (newValue.all { it.isDigit() }) {
                        fontWeightValue = newValue.toInt().coerceIn(1, 1000)
                        fontWeightText = fontWeightValue.toString()
                    }
                },
                label = stringResource(Res.string.font_weight),
                useLabelAsPlaceholder = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                trailingIcon = {
                    Text(
                        text = stringResource(Res.string.font_weight),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            )
            Slider(
                progress = fontWeightValue.toFloat(),
                onProgressChange = { newValue ->
                    fontWeightValue = newValue.toInt()
                    fontWeightText = newValue.toInt().toString()
                },
                minValue = 1f,
                maxValue = 1000f
            )
        }
        Column(
            modifier = Modifier.weight(0.5f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = fontSizeText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty()) {
                        fontSizeValue = 6
                        fontSizeText = ""
                    } else if (newValue.all { it.isDigit() }) {
                        fontSizeValue = newValue.toInt().coerceIn(6, 96)
                        fontSizeText = fontSizeValue.toString()
                    }
                },
                label = stringResource(Res.string.font_size),
                useLabelAsPlaceholder = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                trailingIcon = {
                    Text(
                        text = stringResource(Res.string.font_size),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            )
            Slider(
                progress = fontSizeValue.toFloat(),
                onProgressChange = { newValue ->
                    fontSizeValue = newValue.toInt()
                    fontSizeText = newValue.toInt().toString()
                },
                minValue = 6f,
                maxValue = 96f
            )
        }
    }
    TextField(
        value = customText,
        onValueChange = { customText = it },
        label = stringResource(Res.string.custom_text),
        useLabelAsPlaceholder = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        modifier = Modifier.padding(top = 12.dp),
        trailingIcon = {
            Text(
                text = if (customText.isEmpty()) stringResource(Res.string.custom_text) else stringResource(Res.string.clear_text),
                fontSize = 14.sp,
                modifier = Modifier
                    .then(
                        if (customText.isNotEmpty())
                            Modifier.clickable(
                                indication = null,
                                interactionSource = null,
                            ) {
                                customText = ""
                            }
                        else Modifier
                    )
                    .padding(horizontal = 16.dp)
            )
        }
    )
    Text(
        modifier = Modifier.padding(top = 12.dp),
        text = customText.ifEmpty { "永 の A 6" },
        fontSize = fontSizeValue.coerceIn(6, 96).sp,
        fontWeight = FontWeight(fontWeightValue.coerceIn(1, 1000)),
    )
}