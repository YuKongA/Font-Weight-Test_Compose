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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fontweighttest.shared.generated.resources.Res
import fontweighttest.shared.generated.resources.unicode_coverage_blocks
import fontweighttest.shared.generated.resources.unicode_coverage_duration
import fontweighttest.shared.generated.resources.unicode_coverage_error
import fontweighttest.shared.generated.resources.unicode_coverage_hide_perfect
import fontweighttest.shared.generated.resources.unicode_coverage_overall
import fontweighttest.shared.generated.resources.unicode_coverage_progress
import fontweighttest.shared.generated.resources.unicode_coverage_running
import fontweighttest.shared.generated.resources.unicode_coverage_start
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import top.yukonga.fontWeightTest.utils.UnicodeCoverageMode
import top.yukonga.fontWeightTest.utils.UnicodeCoverageProgress
import top.yukonga.fontWeightTest.utils.UnicodeCoverageResult
import top.yukonga.fontWeightTest.utils.measureUnicodeCoverage
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import kotlin.math.roundToInt

@Composable
fun UnicodeCoverageView() {
    val coroutineScope = rememberCoroutineScope()
    var isRunning by remember { mutableStateOf(false) }
    var progress by remember {
        mutableStateOf(
            UnicodeCoverageProgress(
                processedCount = 0,
                supportedCount = 0,
                totalCount = 1
            )
        )
    }
    var result by remember { mutableStateOf<UnicodeCoverageResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hidePerfectBlocks by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf(UnicodeCoverageMode.UNIHAN) }
    val modeTabs = listOf("UNIHAN", "UNICODE")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TabRow(
            tabs = modeTabs,
            selectedTabIndex = if (mode == UnicodeCoverageMode.UNIHAN) 0 else 1,
            onTabSelected = { selectedIndex ->
                if (!isRunning) {
                    mode = if (selectedIndex == 0) {
                        UnicodeCoverageMode.UNIHAN
                    } else {
                        UnicodeCoverageMode.UNICODE
                    }
                }
            }
        )

        TextButton(
            text = if (isRunning) {
                stringResource(Res.string.unicode_coverage_running)
            } else {
                stringResource(Res.string.unicode_coverage_start)
            },
            enabled = !isRunning,
            onClick = {
                coroutineScope.launch {
                    isRunning = true
                    result = null
                    errorMessage = null
                    progress = UnicodeCoverageProgress(
                        processedCount = 0,
                        supportedCount = 0,
                        totalCount = 1
                    )
                    runCatching {
                        measureUnicodeCoverage(mode) { current ->
                            progress = current
                        }
                    }.onSuccess { completed ->
                        result = completed
                    }.onFailure { throwable ->
                        errorMessage = throwable.message ?: throwable.toString()
                    }
                    isRunning = false
                }
            },
            colors = ButtonDefaults.textButtonColorsPrimary(),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
    AnimatedVisibility(
        visible = progress.processedCount > 0 && progress.processedCount != progress.totalCount,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 12.dp),
            insideMargin = PaddingValues(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val currentPercent = formatPercentValue(progress.percentage)
                Text(
                    text = stringResource(
                        Res.string.unicode_coverage_progress,
                        progress.processedCount,
                        progress.totalCount,
                        currentPercent
                    )
                )
            }
            errorMessage?.let { message ->
                Text(
                    text = stringResource(Res.string.unicode_coverage_error, message)
                )
            }
        }
    }

    result?.let { completed ->
        val finalPercent = formatPercentValue(completed.percentage)
        val visibleBlocks = completed.blockResults.filter { block ->
            !(hidePerfectBlocks && block.supportedCount == block.totalCount)
        }

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
                        completed.grade,
                        completed.supportedCount,
                        completed.processedCount,
                        finalPercent
                    ),
                    fontWeight = FontWeight.SemiBold
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
                checked = hidePerfectBlocks,
                onCheckedChange = { hidePerfectBlocks = it },
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = block.blockName,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = block.grade,
                                modifier = Modifier.padding(start = 12.dp),
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
