package top.yukonga.fontWeightTest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.yukonga.fontWeightTest.ui.AboutDialog
import top.yukonga.fontWeightTest.ui.HomeView
import top.yukonga.fontWeightTest.ui.SansSerifView
import top.yukonga.fontWeightTest.ui.SerifView
import top.yukonga.fontWeightTest.ui.TuneDialog
import top.yukonga.fontWeightTest.ui.theme.AppTheme
import top.yukonga.fontWeightTest.utils.Preferences
import top.yukonga.fontWeightTest.utils.isCompact
import top.yukonga.fontWeightTest.utils.navigationItems

@SuppressLint("SourceLockedOrientationActivity")
@Composable
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
fun App(
    colorMode: MutableState<Int> = remember { mutableIntStateOf(Preferences().perfGet("colorMode")?.toInt() ?: 0) }
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val selectedItem = remember { mutableIntStateOf(0) }

    val topAppBarScrollBehavior0 = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior1 = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior2 = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(topAppBarScrollBehavior0, topAppBarScrollBehavior1, topAppBarScrollBehavior2)

    val currentScrollBehavior = when (pagerState.currentPage) {
        0 -> topAppBarScrollBehaviorList[0]
        1 -> topAppBarScrollBehaviorList[1]
        2 -> topAppBarScrollBehaviorList[2]
        else -> throw IllegalStateException("Unsupported page")
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.debounce(100).collectLatest { page ->
            selectedItem.intValue = page
        }
    }

    AppTheme(colorMode = colorMode.value) {
        NavigationSuiteScaffold(selectedItem, pagerState) { layoutType ->
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(currentScrollBehavior, colorMode)
                }
            ) { padding ->
                val context = LocalContext.current
                if (layoutType == NavigationSuiteType.NavigationBar) {
                    (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                Column(
                    Modifier.padding(top = padding.calculateTopPadding())
                ) {
                    HorizontalPager(pagerState, layoutType, topAppBarScrollBehaviorList, colorMode.value)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior, colorMode: MutableState<Int>) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                maxLines = 1
            )
        },
        navigationIcon = { AboutDialog() },
        actions = { TuneDialog(colorMode) },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun NavigationSuiteScaffold(
    selectedItem: MutableState<Int>,
    pagerState: PagerState,
    content: @Composable (layoutType: NavigationSuiteType) -> Unit
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val windowSize = with(LocalDensity.current) { currentWindowSize().toSize().toDpSize() }
    val navLayoutType = when {
        adaptiveInfo.windowPosture.isTabletop -> NavigationSuiteType.NavigationBar
        adaptiveInfo.windowSizeClass.isCompact() -> NavigationSuiteType.NavigationBar
        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED && windowSize.width >= 1200.dp -> NavigationSuiteType.NavigationDrawer
        else -> NavigationSuiteType.NavigationRail
    }
    val coroutineScope = rememberCoroutineScope()

    NavigationSuiteScaffold(
        layoutType = navLayoutType,
        navigationSuiteItems = {
            navigationItems.forEachIndexed { index, item ->
                val isSelected = selectedItem.value == index
                item(
                    icon = { Icon(painterResource(if (isSelected) item.selectedIcon else item.normalIcon), contentDescription = stringResource(item.label)) },
                    label = { Text(text = stringResource(item.label)) },
                    alwaysShowLabel = false,
                    selected = isSelected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
    ) {
        content(navLayoutType)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorizontalPager(
    pagerState: PagerState,
    layoutType: NavigationSuiteType,
    topAppBarScrollBehaviorList: List<TopAppBarScrollBehavior>,
    colorMode: Int
) {
    HorizontalPager(
        verticalAlignment = Alignment.Top,
        state = pagerState,
        beyondViewportPageCount = 1,
        pageContent = { page ->
            when (page) {
                0 -> HomeView(layoutType, topAppBarScrollBehaviorList[0], colorMode)
                1 -> SansSerifView(layoutType, topAppBarScrollBehaviorList[1])
                2 -> SerifView(layoutType, topAppBarScrollBehaviorList[2])
            }
        }
    )
}