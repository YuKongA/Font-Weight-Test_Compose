package top.yukonga.fontWeightTest

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import fontweighttest.composeapp.generated.resources.Res
import fontweighttest.composeapp.generated.resources.app_name
import fontweighttest.composeapp.generated.resources.home
import fontweighttest.composeapp.generated.resources.monospace
import fontweighttest.composeapp.generated.resources.sans_serif
import fontweighttest.composeapp.generated.resources.serif
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import top.yukonga.fontWeightTest.ui.AboutDialog
import top.yukonga.fontWeightTest.ui.HomeView
import top.yukonga.fontWeightTest.ui.MonospaceView
import top.yukonga.fontWeightTest.ui.SansSerifView
import top.yukonga.fontWeightTest.ui.SerifView
import top.yukonga.fontWeightTest.ui.theme.AppTheme
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(FlowPreview::class)
@Composable
fun App(
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    AppTheme(
        isDarkTheme = isDarkTheme
    ) {
        val coroutineScope = rememberCoroutineScope()
        val topAppBarScrollBehaviorList = List(4) { MiuixScrollBehavior() }
        val pagerState = rememberPagerState(pageCount = { 4 })
        var selectedPage by remember { mutableIntStateOf(pagerState.currentPage) }
        val currentScrollBehavior = topAppBarScrollBehaviorList[selectedPage]

        LaunchedEffect(pagerState.settledPage) {
            if (selectedPage != pagerState.settledPage) selectedPage = pagerState.settledPage
        }

        val navigationItems = listOf(
            NavigationItem(
                stringResource(Res.string.home),
                vectorResource(Res.drawable.home)
            ),
            NavigationItem(
                stringResource(Res.string.sans_serif),
                vectorResource(Res.drawable.sans_serif)
            ),
            NavigationItem(
                stringResource(Res.string.serif),
                vectorResource(Res.drawable.serif)
            ),
            NavigationItem(
                stringResource(Res.string.monospace),
                vectorResource(Res.drawable.monospace)
            ),
        )

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
                BoxWithConstraints {
                    if (maxWidth < 768.dp) {
                        TopAppBar(
                            color = Color.Transparent,
                            modifier = Modifier
                                .hazeEffect(hazeState) {
                                    style = hazeStyleTopAppBar
                                    blurRadius = 25.dp
                                    noiseFactor = 0f
                                },
                            title = stringResource(Res.string.app_name),
                            navigationIcon = { AboutDialog() },
                            scrollBehavior = currentScrollBehavior
                        )
                    } else {
                        SmallTopAppBar(
                            color = Color.Transparent,
                            modifier = Modifier
                                .hazeEffect(hazeState) {
                                    style = hazeStyleTopAppBar
                                    blurRadius = 25.dp
                                    noiseFactor = 0f
                                },
                            title = stringResource(Res.string.app_name),
                            navigationIcon = { AboutDialog() },
                            scrollBehavior = currentScrollBehavior
                        )
                    }
                }
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
                    selected = selectedPage,
                    onClick = { index ->
                        selectedPage = index
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
                state = pagerState,
                userScrollEnabled = false,
                pageContent = { page ->
                    key(page) {
                        when (page) {
                            0 -> HomeView(topAppBarScrollBehaviorList[0], padding)
                            1 -> SansSerifView(topAppBarScrollBehaviorList[1], padding)
                            2 -> SerifView(topAppBarScrollBehaviorList[2], padding)
                            3 -> MonospaceView(topAppBarScrollBehaviorList[3], padding)
                        }
                    }
                }
            )
        }
    }
}