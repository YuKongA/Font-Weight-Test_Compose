package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
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
import top.yukonga.fontWeightTest.utils.fontWeightDescriptions
import top.yukonga.fontWeightTest.utils.fontWeightsList
import top.yukonga.fontWeightTest.utils.getOptimizedFontWeight
import top.yukonga.fontWeightTest.utils.miSansList
import top.yukonga.fontWeightTest.utils.testCharacters
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Stable
data class FontDisplayState(
    val customText: String = "",
    val fontSizeValue: Int = 24,
    val fontWeightValue: Int = 400
) {
    val effectiveFontSize = fontSizeValue.coerceIn(6, 96)
    val effectiveFontWeight by lazy {
        getOptimizedFontWeight(fontWeightValue.coerceIn(1, 999))
    }
    val displayText = customText.ifEmpty { "永 の A 6" }
}

@Composable
fun HomeView(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {
    val focusManager = LocalFocusManager.current
    val layoutDirection = LocalLayoutDirection.current

    LazyColumn(
        modifier = Modifier
            .scrollEndHaptic()
            .overScrollVertical()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .clickable(
                indication = null,
                interactionSource = null,
                onClick = { focusManager.clearFocus() }
            ),
        contentPadding = PaddingValues(
            top = padding.calculateTopPadding() + 12.dp,
            start = padding.calculateStartPadding(layoutDirection),
            end = padding.calculateEndPadding(layoutDirection),
            bottom = padding.calculateBottomPadding() + 12.dp
        ),
    ) {
        homeContent()
    }
}

private fun LazyListScope.homeContent() {
    item(key = "all_weights") {
        CardView {
            AllWeightText()
        }
    }

    item(key = "comparison_title") {
        SmallTitle(
            text = stringResource(Res.string.comparison_display),
            modifier = Modifier.padding(top = 6.dp)
        )
    }

    item(key = "comparison_display") {
        CardView {
            ComparisonDisplay()
        }
    }

    item(key = "variable_font_title") {
        SmallTitle(
            text = stringResource(Res.string.variable_font),
            modifier = Modifier.padding(top = 6.dp)
        )
    }

    item(key = "variable_font") {
        CardView {
            SliderTestView()
        }
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
        fontWeightsList.forEachIndexed { index, fontWeight ->
            WeightText(
                "${(index + 1) * 100} - ${fontWeightDescriptions[index]}",
                fontWeight
            )
        }
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
        testCharacters.forEach { MiSansTest(it) }
    }
}

@Composable
fun MiSansTest(text: String) {
    val fontList = remember { miSansList }
    val weightList = remember { fontWeightsList }

    Row {
        weightList.forEachIndexed { index, fontWeight ->
            Text(
                text = text,
                fontWeight = fontWeight,
                fontFamily = FontFamily(Font(fontList[index], weight = fontWeight))
            )
        }
    }
}

@Composable
fun DeviceFontTestView(text: String) {
    Column {
        Text(text = text)
        testCharacters.forEach { MoreTestText(it) }
    }
}

@Composable
fun MoreTestText(text: String) {
    val weightList = remember { fontWeightsList }

    Row {
        weightList.forEach { fontWeight ->
            Text(
                text = text,
                fontWeight = fontWeight
            )
        }
    }
}

@Composable
fun SliderTestView() {
    var customText by rememberSaveable { mutableStateOf("") }
    var fontSizeValue by rememberSaveable { mutableIntStateOf(24) }
    var fontSizeText by rememberSaveable { mutableStateOf(fontSizeValue.toString()) }
    var fontWeightValue by rememberSaveable { mutableIntStateOf(400) }
    var fontWeightText by rememberSaveable { mutableStateOf(fontWeightValue.toString()) }

    val fontDisplayState by remember(customText, fontSizeValue, fontWeightValue) {
        derivedStateOf {
            FontDisplayState(
                customText = customText,
                fontSizeValue = fontSizeValue,
                fontWeightValue = fontWeightValue
            )
        }
    }

    val focusManager = LocalFocusManager.current

    val onFontWeightTextChange = remember {
        { newValue: String ->
            if (newValue.isEmpty()) {
                fontWeightValue = 1
                fontWeightText = ""
            } else if (newValue.all { it.isDigit() }) {
                fontWeightValue = newValue.toInt().coerceIn(1, 1000)
                fontWeightText = fontWeightValue.toString()
            }
        }
    }

    val onFontWeightSliderChange = remember {
        { newValue: Float ->
            fontWeightValue = newValue.toInt()
            fontWeightText = newValue.toInt().toString()
        }
    }

    val onFontSizeTextChange = remember {
        { newValue: String ->
            if (newValue.isEmpty()) {
                fontSizeValue = 6
                fontSizeText = ""
            } else if (newValue.all { it.isDigit() }) {
                fontSizeValue = newValue.toInt().coerceIn(6, 96)
                fontSizeText = fontSizeValue.toString()
            }
        }
    }

    val onFontSizeSliderChange = remember {
        { newValue: Float ->
            fontSizeValue = newValue.toInt()
            fontSizeText = newValue.toInt().toString()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FontWeightControl(
                modifier = Modifier.weight(0.5f),
                value = fontWeightText,
                onValueChange = onFontWeightTextChange,
                sliderValue = fontWeightValue.toFloat(),
                onSliderChange = onFontWeightSliderChange,
                focusManager = focusManager
            )

            FontSizeControl(
                modifier = Modifier.weight(0.5f),
                value = fontSizeText,
                onValueChange = onFontSizeTextChange,
                sliderValue = fontSizeValue.toFloat(),
                onSliderChange = onFontSizeSliderChange,
                focusManager = focusManager
            )
        }

        CustomTextInput(
            value = customText,
            onValueChange = { customText = it },
            focusManager = focusManager
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = fontDisplayState.displayText,
            fontSize = fontDisplayState.effectiveFontSize.sp,
            fontWeight = fontDisplayState.effectiveFontWeight,
        )
    }
}

@Composable
private fun FontWeightControl(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    focusManager: FocusManager
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
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
            value = sliderValue,
            onValueChange = onSliderChange,
            valueRange = 1f..1000f
        )
    }
}

@Composable
private fun FontSizeControl(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    focusManager: FocusManager
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
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
            value = sliderValue,
            onValueChange = onSliderChange,
            valueRange = 6f..96f
        )
    }
}

@Composable
private fun CustomTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    focusManager: FocusManager
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = stringResource(Res.string.custom_text),
        useLabelAsPlaceholder = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        trailingIcon = {
            val clearText = stringResource(Res.string.clear_text)
            val customTextLabel = stringResource(Res.string.custom_text)
            Text(
                text = if (value.isEmpty()) customTextLabel else clearText,
                fontSize = 14.sp,
                modifier = Modifier
                    .then(
                        if (value.isNotEmpty())
                            Modifier.clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = { onValueChange("") }
                            )
                        else Modifier
                    )
                    .padding(horizontal = 16.dp)
            )
        }
    )
}