package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import fontweighttest.shared.generated.resources.MiSansVF
import fontweighttest.shared.generated.resources.Res
import fontweighttest.shared.generated.resources.clear_text
import fontweighttest.shared.generated.resources.comparison_display
import fontweighttest.shared.generated.resources.custom_text
import fontweighttest.shared.generated.resources.device_font
import fontweighttest.shared.generated.resources.font_size
import fontweighttest.shared.generated.resources.font_weight
import fontweighttest.shared.generated.resources.variable_font
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import top.yukonga.fontWeightTest.ui.components.CardView
import top.yukonga.fontWeightTest.ui.components.NativeVariableText
import top.yukonga.fontWeightTest.ui.viewmodel.HomeViewModel
import top.yukonga.fontWeightTest.utils.fontWeightDescriptions
import top.yukonga.fontWeightTest.utils.fontWeightsList
import top.yukonga.fontWeightTest.utils.testCharacters
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun HomePage(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    viewModel: HomeViewModel = viewModel { HomeViewModel() }
) {
    val focusManager = LocalFocusManager.current
    val layoutDirection = LocalLayoutDirection.current

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
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
        overscrollEffect = null,
    ) {
        homeContent(viewModel)
    }
}

private fun LazyListScope.homeContent(viewModel: HomeViewModel) {
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
            SliderTestView(viewModel)
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
    NativeVariableText(
        text = description,
        fontSize = 16f,
        fontWeight = fontWeight.weight,
        color = MiuixTheme.colorScheme.onBackground,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth()
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
    Row {
        fontWeightsList.forEach { fontWeight ->
            Text(
                text = text,
                fontWeight = fontWeight,
                fontFamily = FontFamily(Font(Res.font.MiSansVF, weight = fontWeight))
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
    Row {
        fontWeightsList.forEach { fontWeight ->
            NativeVariableText(
                text = text,
                fontSize = 16f,
                fontWeight = fontWeight.weight,
                color = MiuixTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun SliderTestView(
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FontWeightControl(
                modifier = Modifier.weight(0.5f),
                value = uiState.fontWeightText,
                onValueChange = viewModel::updateFontWeightText,
                sliderValue = uiState.fontWeightValue.toFloat(),
                onSliderChange = viewModel::updateFontWeightSlider,
                focusManager = focusManager
            )

            FontSizeControl(
                modifier = Modifier.weight(0.5f),
                value = uiState.fontSizeText,
                onValueChange = viewModel::updateFontSizeText,
                sliderValue = uiState.fontSizeValue.toFloat(),
                onSliderChange = viewModel::updateFontSizeSlider,
                focusManager = focusManager
            )
        }

        CustomTextInput(
            value = uiState.customText,
            onValueChange = viewModel::updateCustomText,
            focusManager = focusManager
        )

        NativeVariableText(
            text = uiState.fontDisplayState.displayText,
            fontSize = uiState.fontDisplayState.effectiveFontSize.toFloat(),
            fontWeight = uiState.fontDisplayState.effectiveFontWeight.weight,
            color = MiuixTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
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
            label = "400",
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
            label = "24",
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
            valueRange = 1f..96f
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
        label = "永 の 한 A 6",
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
