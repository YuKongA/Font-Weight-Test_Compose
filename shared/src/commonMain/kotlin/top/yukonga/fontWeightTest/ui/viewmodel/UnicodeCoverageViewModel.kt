package top.yukonga.fontWeightTest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import top.yukonga.fontWeightTest.utils.UnicodeCoverageMode
import top.yukonga.fontWeightTest.utils.UnicodeCoverageProgress
import top.yukonga.fontWeightTest.utils.UnicodeCoverageResult
import top.yukonga.fontWeightTest.utils.codePointCount
import top.yukonga.fontWeightTest.utils.convertCodePointToString
import top.yukonga.fontWeightTest.utils.measureUnicodeCoverage
import top.yukonga.fontWeightTest.utils.takeCodePoints

data class UnicodeCoverageUiState(
    val isRunning: Boolean = false,
    val mode: UnicodeCoverageMode = UnicodeCoverageMode.UNIHAN,
    val progress: Map<UnicodeCoverageMode, UnicodeCoverageProgress> = mapOf(
        UnicodeCoverageMode.UNIHAN to UnicodeCoverageProgress(0, 0, 1, 0),
        UnicodeCoverageMode.UNICODE to UnicodeCoverageProgress(0, 0, 1, 0)
    ),
    val results: Map<UnicodeCoverageMode, UnicodeCoverageResult?> = emptyMap(),
    val errors: Map<UnicodeCoverageMode, String?> = emptyMap(),
    val displayChars: Map<UnicodeCoverageMode, String> = emptyMap(),
    val hidePerfectBlocks: Boolean = false
)

class UnicodeCoverageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UnicodeCoverageUiState())
    val uiState = _uiState.asStateFlow()

    fun updateMode(mode: UnicodeCoverageMode) {
        if (!_uiState.value.isRunning) {
            _uiState.update { it.copy(mode = mode) }
        }
    }

    fun toggleHidePerfectBlocks(hide: Boolean) {
        _uiState.update { it.copy(hidePerfectBlocks = hide) }
    }

    fun startCoverage() {
        if (_uiState.value.isRunning) return

        val currentMode = _uiState.value.mode
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRunning = true,
                    results = it.results + (currentMode to null),
                    errors = it.errors + (currentMode to null),
                    displayChars = it.displayChars + (currentMode to ""),
                    progress = it.progress + (currentMode to UnicodeCoverageProgress(0, 0, 1, 0))
                )
            }

            runCatching {
                measureUnicodeCoverage(currentMode) { progress ->
                    _uiState.update { state ->
                        // 更新 progress
                        val newProgressMap = state.progress + (currentMode to progress)

                        // 更新 displayChars
                        var newDisplayCharsMap = state.displayChars
                        if (progress.currentChunk.isNotEmpty()) {
                            val chunk = progress.currentChunk
                            val start = maxOf(0, chunk.size - 128)
                            val relevantChunk = chunk.sliceArray(start until chunk.size)
                            val sb = StringBuilder()
                            for (i in relevantChunk.indices.reversed()) {
                                sb.append(convertCodePointToString(relevantChunk[i]))
                            }
                            val newChars = sb.toString()
                            val current = state.displayChars[currentMode] ?: ""
                            val combined = newChars + current
                            val codePoints = combined.codePointCount()
                            val finalChars = if (codePoints > 128) {
                                combined.takeCodePoints(128)
                            } else {
                                combined
                            }
                            newDisplayCharsMap = state.displayChars + (currentMode to finalChars)
                        }

                        state.copy(
                            progress = newProgressMap,
                            displayChars = newDisplayCharsMap
                        )
                    }
                }
            }.onSuccess { completed ->
                _uiState.update {
                    it.copy(
                        isRunning = false,
                        results = it.results + (currentMode to completed)
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isRunning = false,
                        errors = it.errors + (currentMode to (throwable.message ?: throwable.toString()))
                    )
                }
            }
        }
    }
}
