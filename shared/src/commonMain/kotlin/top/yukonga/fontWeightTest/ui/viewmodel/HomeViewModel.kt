package top.yukonga.fontWeightTest.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import top.yukonga.fontWeightTest.utils.FontDisplayState
import top.yukonga.fontWeightTest.utils.parseUnicodeNotationToText

data class HomeUiState(
    val customText: String = "",
    val fontSizeValue: Int = 24,
    val fontSizeText: String = "24",
    val fontWeightValue: Int = 400,
    val fontWeightText: String = "400"
) {
    val fontDisplayState: FontDisplayState
        get() = FontDisplayState(
            customText = customText,
            fontSizeValue = fontSizeValue,
            fontWeightValue = fontWeightValue
        )
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun updateCustomText(text: String) {
        _uiState.update { it.copy(customText = parseUnicodeNotationToText(text)) }
    }

    fun updateFontSizeText(text: String) {
        if (text.isEmpty()) {
            _uiState.update { it.copy(fontSizeValue = 1, fontSizeText = "") }
        } else if (text.all { it.isDigit() }) {
            val value = text.toInt().coerceIn(1, 96)
            _uiState.update { it.copy(fontSizeValue = value, fontSizeText = value.toString()) }
        }
    }

    fun updateFontSizeSlider(value: Float) {
        val intValue = value.toInt()
        _uiState.update { it.copy(fontSizeValue = intValue, fontSizeText = intValue.toString()) }
    }

    fun updateFontWeightText(text: String) {
        if (text.isEmpty()) {
            _uiState.update { it.copy(fontWeightValue = 1, fontWeightText = "") }
        } else if (text.all { it.isDigit() }) {
            val value = text.toInt().coerceIn(1, 1000)
            _uiState.update { it.copy(fontWeightValue = value, fontWeightText = value.toString()) }
        }
    }

    fun updateFontWeightSlider(value: Float) {
        val intValue = value.toInt()
        _uiState.update { it.copy(fontWeightValue = intValue, fontWeightText = intValue.toString()) }
    }
}
