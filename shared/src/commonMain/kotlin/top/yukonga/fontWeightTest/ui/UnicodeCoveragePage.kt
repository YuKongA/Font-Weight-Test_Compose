package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fontweighttest.shared.generated.resources.Res
import fontweighttest.shared.generated.resources.unicode_coverage
import org.jetbrains.compose.resources.stringResource
import top.yukonga.fontWeightTest.ui.components.UnicodeCoverageView
import top.yukonga.fontWeightTest.ui.viewmodel.UnicodeCoverageViewModel
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun UnicodeCoveragePage(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    viewModel: UnicodeCoverageViewModel = viewModel { UnicodeCoverageViewModel() }
) {
    val layoutDirection = LocalLayoutDirection.current
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .scrollEndHaptic()
            .overScrollVertical()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(
            top = padding.calculateTopPadding(),
            start = padding.calculateStartPadding(layoutDirection),
            end = padding.calculateEndPadding(layoutDirection),
            bottom = padding.calculateBottomPadding() + 12.dp
        ),
    ) {
        item(key = "unicode_coverage_title") {
            SmallTitle(text = stringResource(Res.string.unicode_coverage))
        }
        item(key = "unicode_coverage") {
            UnicodeCoverageView(viewModel)
        }
    }
}
