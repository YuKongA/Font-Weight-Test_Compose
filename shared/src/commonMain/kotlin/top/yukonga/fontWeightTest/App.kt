package top.yukonga.fontWeightTest

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import fontweighttest.shared.generated.resources.Res
import fontweighttest.shared.generated.resources.app_name
import fontweighttest.shared.generated.resources.home
import fontweighttest.shared.generated.resources.monospace
import fontweighttest.shared.generated.resources.sans_serif
import fontweighttest.shared.generated.resources.serif
import fontweighttest.shared.generated.resources.tune
import fontweighttest.shared.generated.resources.unicode_nav
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import top.yukonga.fontWeightTest.ui.AboutDialog
import top.yukonga.fontWeightTest.ui.HomePage
import top.yukonga.fontWeightTest.ui.MonospacePage
import top.yukonga.fontWeightTest.ui.SansSerifPage
import top.yukonga.fontWeightTest.ui.SerifPage
import top.yukonga.fontWeightTest.ui.UnicodeCoveragePage
import top.yukonga.fontWeightTest.ui.theme.AppTheme
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme

val LocalPagerState = compositionLocalOf<PagerState> { error("No pager state") }
val LocalHandlePageChange = compositionLocalOf<(Int) -> Unit> { error("No handle page change") }

@Composable
fun App(
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    AppTheme(
        isDarkTheme = isDarkTheme
    ) {
        val coroutineScope = rememberCoroutineScope()
        val topAppBarScrollBehaviorList = List(5) { MiuixScrollBehavior() }

        val pagerState = rememberPagerState(pageCount = { 5 })

        val currentScrollBehavior by remember {
            derivedStateOf { topAppBarScrollBehaviorList[pagerState.currentPage] }
        }

        val handlePageChange: (Int) -> Unit = remember(pagerState, coroutineScope) {
            { page ->
                coroutineScope.launch { pagerState.animateScrollToPage(page) }
            }
        }

        val navigationItems = listOf(
            NavigationItem(stringResource(Res.string.home), vectorResource(Res.drawable.home)),
            NavigationItem(stringResource(Res.string.sans_serif), vectorResource(Res.drawable.sans_serif)),
            NavigationItem(stringResource(Res.string.serif), vectorResource(Res.drawable.serif)),
            NavigationItem(stringResource(Res.string.monospace), vectorResource(Res.drawable.monospace)),
            NavigationItem(stringResource(Res.string.unicode_nav), vectorResource(Res.drawable.tune)),
        )

        val hazeState = remember { HazeState() }
        val surface = MiuixTheme.colorScheme.surface
        val hazeStyle = remember(surface) {
            HazeStyle(
                backgroundColor = surface,
                tint = HazeTint(surface.copy(0.67f))
            )
        }

        CompositionLocalProvider(
            LocalPagerState provides pagerState,
            LocalHandlePageChange provides handlePageChange,
        ) {
            val page by remember { derivedStateOf { pagerState.targetPage } }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBarContent(
                        currentScrollBehavior = currentScrollBehavior,
                        hazeState = hazeState,
                        hazeStyle = hazeStyle
                    )
                },
                bottomBar = {
                    NavigationBar(
                        color = Color.Transparent,
                        modifier = Modifier.hazeEffect(hazeState) {
                            style = hazeStyle
                            blurRadius = 25.dp
                            noiseFactor = 0f
                        }
                    ) {
                        navigationItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = page == index,
                                onClick = { handlePageChange(index) },
                                icon = item.icon,
                                label = item.label,
                            )
                        }
                    }
                }
            ) { padding ->
                PagerContent(
                    pagerState = pagerState,
                    hazeState = hazeState,
                    topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                    padding = padding
                )
            }
        }
    }
}

@Composable
private fun TopAppBarContent(
    currentScrollBehavior: ScrollBehavior,
    hazeState: HazeState,
    hazeStyle: HazeStyle
) {
    BoxWithConstraints {
        val isCompact = maxWidth < 768.dp
        val modifier = Modifier.hazeEffect(hazeState) {
            style = hazeStyle
            blurRadius = 25.dp
            noiseFactor = 0f
        }

        if (isCompact) {
            TopAppBar(
                color = Color.Transparent,
                modifier = modifier,
                title = stringResource(Res.string.app_name),
                navigationIcon = { AboutDialog() },
                scrollBehavior = currentScrollBehavior
            )
        } else {
            SmallTopAppBar(
                color = Color.Transparent,
                modifier = modifier,
                title = stringResource(Res.string.app_name),
                navigationIcon = { AboutDialog() },
                scrollBehavior = currentScrollBehavior
            )
        }
    }
}

@Composable
private fun PagerContent(
    pagerState: PagerState,
    hazeState: HazeState,
    topAppBarScrollBehaviorList: List<ScrollBehavior>,
    padding: PaddingValues
) {
    HorizontalPager(
        modifier = Modifier.hazeSource(state = hazeState),
        state = pagerState,
        beyondViewportPageCount = 5,
        userScrollEnabled = false,
        pageContent = { page ->
            key(page) {
                when (page) {
                    0 -> HomePage(topAppBarScrollBehaviorList[0], padding)
                    1 -> SansSerifPage(topAppBarScrollBehaviorList[1], padding)
                    2 -> SerifPage(topAppBarScrollBehaviorList[2], padding)
                    3 -> MonospacePage(topAppBarScrollBehaviorList[3], padding)
                    4 -> UnicodeCoveragePage(topAppBarScrollBehaviorList[4], padding)
                }
            }
        }
    )
}
