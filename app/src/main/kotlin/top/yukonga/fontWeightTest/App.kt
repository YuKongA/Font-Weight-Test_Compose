package top.yukonga.fontWeightTest

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.yukonga.fontWeightTest.ui.AboutDialog
import top.yukonga.fontWeightTest.ui.HomeView
import top.yukonga.fontWeightTest.ui.MonospaceView
import top.yukonga.fontWeightTest.ui.SansSerifView
import top.yukonga.fontWeightTest.ui.SerifView
import top.yukonga.fontWeightTest.ui.theme.AppTheme
import top.yukonga.miuix.kmp.basic.HorizontalPager
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(FlowPreview::class)
@Composable
fun App() {
    AppTheme {
        val topAppBarScrollBehavior0 = MiuixScrollBehavior(rememberTopAppBarState())
        val topAppBarScrollBehavior1 = MiuixScrollBehavior(rememberTopAppBarState())
        val topAppBarScrollBehavior2 = MiuixScrollBehavior(rememberTopAppBarState())
        val topAppBarScrollBehavior3 = MiuixScrollBehavior(rememberTopAppBarState())

        val topAppBarScrollBehaviorList = listOf(
            topAppBarScrollBehavior0,
            topAppBarScrollBehavior1,
            topAppBarScrollBehavior2,
            topAppBarScrollBehavior3
        )

        val pagerState = rememberPagerState(pageCount = { 4 })
        var targetPage by remember { mutableIntStateOf(pagerState.currentPage) }
        val coroutineScope = rememberCoroutineScope()

        val currentScrollBehavior = when (pagerState.currentPage) {
            0 -> topAppBarScrollBehaviorList[0]
            1 -> topAppBarScrollBehaviorList[1]
            2 -> topAppBarScrollBehaviorList[2]
            else -> topAppBarScrollBehaviorList[3]
        }

        val navigationItems = listOf(
            NavigationItem(
                stringResource(R.string.home),
                ImageVector.vectorResource(R.drawable.home)
            ),
            NavigationItem(
                stringResource(R.string.sans_serif),
                ImageVector.vectorResource(R.drawable.sans_serif)
            ),
            NavigationItem(
                stringResource(R.string.serif),
                ImageVector.vectorResource(R.drawable.serif)
            ),
            NavigationItem(
                stringResource(R.string.monospace),
                ImageVector.vectorResource(R.drawable.monospace)
            ),
        )

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest { page ->
                targetPage = page
            }
        }

        val hazeState = remember { HazeState() }

        val hazeStyleTopAppBar = HazeStyle(
            backgroundColor = MiuixTheme.colorScheme.background,
            tint = HazeTint(
                MiuixTheme.colorScheme.background.copy(
                    if (currentScrollBehavior.state.collapsedFraction <= 0f) 1f
                    else lerp(1f, 0.67f, (currentScrollBehavior.state.collapsedFraction))
                )
            )
        )

        val hazeStyleNavigationBar = HazeStyle(
            backgroundColor = MiuixTheme.colorScheme.background,
            tint = HazeTint(MiuixTheme.colorScheme.background.copy(0.67f))
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    color = Color.Transparent,
                    modifier = Modifier
                        .hazeEffect(hazeState) {
                            style = hazeStyleTopAppBar
                            blurRadius = 25.dp
                            noiseFactor = 0f
                        },
                    title = stringResource(R.string.app_name),
                    navigationIcon = { AboutDialog() },
                    scrollBehavior = currentScrollBehavior
                )
            },
            bottomBar = {
                NavigationBar(
                    color = Color.Transparent,
                    modifier = Modifier.hazeEffect(hazeState) {
                        style = hazeStyleNavigationBar
                        blurRadius = 25.dp
                        noiseFactor = 0f
                    },
                    items = navigationItems,
                    selected = targetPage,
                    onClick = { index ->
                        targetPage = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        ) { padding ->
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState),
                pagerState = pagerState,
                pageContent = { page ->
                    when (page) {
                        0 -> HomeView(topAppBarScrollBehaviorList[0], padding)
                        1 -> SansSerifView(topAppBarScrollBehaviorList[1], padding)
                        2 -> SerifView(topAppBarScrollBehaviorList[2], padding)
                        3 -> MonospaceView(topAppBarScrollBehaviorList[3], padding)
                    }
                }
            )
        }
    }
}