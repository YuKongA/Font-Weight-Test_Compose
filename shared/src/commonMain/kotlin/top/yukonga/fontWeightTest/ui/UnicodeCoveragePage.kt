package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fontweighttest.shared.generated.resources.Res
import fontweighttest.shared.generated.resources.unicode_nav
import org.jetbrains.compose.resources.stringResource
import top.yukonga.fontWeightTest.ui.components.PageScaffold
import top.yukonga.fontWeightTest.ui.components.UnicodeCoverageView
import top.yukonga.fontWeightTest.ui.viewmodel.UnicodeCoverageViewModel
import top.yukonga.miuix.kmp.basic.ScrollBehavior

@Composable
fun UnicodeCoveragePage(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    viewModel: UnicodeCoverageViewModel = viewModel { UnicodeCoverageViewModel() }
) {
    PageScaffold(
        title = stringResource(Res.string.unicode_nav),
        topAppBarScrollBehavior = topAppBarScrollBehavior,
        padding = padding,
        topPaddingExtra = 0.dp
    ) {
        item(key = "unicode_coverage") {
            UnicodeCoverageView(viewModel)
        }
    }
}
