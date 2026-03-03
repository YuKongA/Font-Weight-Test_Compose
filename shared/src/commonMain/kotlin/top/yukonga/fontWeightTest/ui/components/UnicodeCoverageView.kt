package top.yukonga.fontWeightTest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fontweighttest.shared.generated.resources.Res
import fontweighttest.shared.generated.resources.unicode_coverage_blocks
import fontweighttest.shared.generated.resources.unicode_coverage_current
import fontweighttest.shared.generated.resources.unicode_coverage_duration
import fontweighttest.shared.generated.resources.unicode_coverage_error
import fontweighttest.shared.generated.resources.unicode_coverage_hide_perfect
import fontweighttest.shared.generated.resources.unicode_coverage_overall
import fontweighttest.shared.generated.resources.unicode_coverage_progress
import fontweighttest.shared.generated.resources.unicode_coverage_ratio
import fontweighttest.shared.generated.resources.unicode_coverage_running
import fontweighttest.shared.generated.resources.unicode_coverage_start
import fontweighttest.shared.generated.resources.unicode_coverage_version
import org.jetbrains.compose.resources.stringResource
import top.yukonga.fontWeightTest.ui.viewmodel.UnicodeCoverageViewModel
import top.yukonga.fontWeightTest.utils.UnicodeCoverageMode
import top.yukonga.fontWeightTest.utils.convertCodePointToString
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import kotlin.math.roundToInt

@Composable
fun UnicodeCoverageView(
    viewModel: UnicodeCoverageViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val modeTabs = listOf("UNIHAN", "UNICODE")

    val currentProgress = uiState.progress[uiState.mode]!!
    val currentResult = uiState.results[uiState.mode]
    val currentError = uiState.errors[uiState.mode]
    val currentDisplayChars = uiState.displayChars[uiState.mode] ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TabRow(
            tabs = modeTabs,
            selectedTabIndex = if (uiState.mode == UnicodeCoverageMode.UNIHAN) 0 else 1,
            onTabSelected = { selectedIndex ->
                if (!uiState.isRunning) {
                    viewModel.updateMode(if (selectedIndex == 0) UnicodeCoverageMode.UNIHAN else UnicodeCoverageMode.UNICODE)
                }
            }
        )

        TextButton(
            text = if (uiState.isRunning) {
                stringResource(Res.string.unicode_coverage_running)
            } else {
                stringResource(Res.string.unicode_coverage_start)
            },
            enabled = !uiState.isRunning,
            onClick = viewModel::startCoverage,
            colors = ButtonDefaults.textButtonColorsPrimary(),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
    AnimatedVisibility(
        visible = currentProgress.processedCount > 0 && currentProgress.processedCount != currentProgress.totalCount,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Column(
            modifier = Modifier
                .padding(all = 12.dp),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                insideMargin = PaddingValues(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val currentPercent = formatPercentValue(currentProgress.percentage)
                    Text(
                        text = stringResource(
                            Res.string.unicode_coverage_progress,
                            currentProgress.processedCount,
                            currentProgress.totalCount,
                            currentPercent
                        )
                    )
                    if (currentProgress.currentCodePoint > 0) {
                        Text(
                            text = stringResource(
                                Res.string.unicode_coverage_current,
                                convertCodePointToString(currentProgress.currentCodePoint),
                                currentProgress.currentCodePoint.toString(16).uppercase()
                            )
                        )
                    }
                }
                currentError?.let { message ->
                    Text(
                        text = stringResource(Res.string.unicode_coverage_error, message)
                    )
                }
            }
            if (currentDisplayChars.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    text = currentDisplayChars,
                    textAlign = TextAlign.Center,
                    fontSize = 34.sp,
                    lineHeight = 38.sp
                )
            }
        }
    }

    currentResult?.let { completed ->
        val finalPercent = formatPercentValue(completed.percentage)
        val visibleBlocks = completed.blockResults
            .filter { block ->
                !(uiState.hidePerfectBlocks && block.supportedCount == block.totalCount)
            }
            .sortedByDescending { it.percentage }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 12.dp),
            insideMargin = PaddingValues(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(
                        Res.string.unicode_coverage_overall,
                        completed.grade
                    ),
                    color = getGradeColor(completed.grade),
                    fontWeight = FontWeight.SemiBold
                )
                completed.unicodeVersionMetadata?.let { metadata ->
                    Text(
                        text = stringResource(
                            Res.string.unicode_coverage_version,
                            metadata.version,
                            metadata.date
                        )
                    )
                }
                Text(
                    stringResource(
                        Res.string.unicode_coverage_ratio,
                        completed.supportedCount,
                        completed.totalCount,
                        finalPercent,
                    )
                )
                Text(
                    text = stringResource(
                        Res.string.unicode_coverage_duration,
                        completed.durationMillis
                    )
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        ) {
            SuperCheckbox(
                title = stringResource(Res.string.unicode_coverage_hide_perfect),
                checked = uiState.hidePerfectBlocks,
                onCheckedChange = viewModel::toggleHidePerfectBlocks,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        SmallTitle(
            modifier = Modifier.padding(top = 6.dp),
            text = stringResource(Res.string.unicode_coverage_blocks),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            visibleBlocks.forEach { block ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = PaddingValues(18.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = block.blockName,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = block.grade,
                                modifier = Modifier.padding(start = 12.dp),
                                color = getGradeColor(block.grade),
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "${block.supportedCount} / ${block.totalCount} (${formatPercentValue(block.percentage)}%)",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private fun formatPercentValue(value: Double): String {
    val scaled = (value * 100).roundToInt()
    val whole = scaled / 100
    val fraction = (scaled % 100).toString().padStart(2, '0')
    return "$whole.$fraction"
}

private fun getGradeColor(grade: String): Color {
    return when (grade) {
        "PG" -> Color(0xFF00BCD4) // Cyan
        "EX" -> Color(0xFF4CAF50) // Green
        "A" -> Color(0xFF8BC34A) // Light Green
        "B" -> Color(0xFFCDDC39) // Lime
        "C" -> Color(0xFFFFEB3B) // Yellow
        "D" -> Color(0xFFFFC107) // Amber
        "E" -> Color(0xFFFF9800) // Orange
        "F" -> Color(0xFFF44336) // Red
        else -> Color.Unspecified
    }
}
