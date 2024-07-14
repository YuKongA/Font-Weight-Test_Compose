package top.yukonga.fontWeightTest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.window.core.layout.WindowWidthSizeClass
import top.yukonga.fontWeightTest.misc.isCompact
import top.yukonga.fontWeightTest.misc.navigationItems
import top.yukonga.fontWeightTest.ui.AboutDialog
import top.yukonga.fontWeightTest.ui.HomeView
import top.yukonga.fontWeightTest.ui.SansSerifView
import top.yukonga.fontWeightTest.ui.SerifView
import top.yukonga.fontWeightTest.ui.theme.AppTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun App() {
    val pagerState = rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f, pageCount = { 3 })
    val selectedItem = remember { mutableIntStateOf(0) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val isClickBottomBarChange = remember { mutableStateOf(false) }

    LaunchedEffect(selectedItem.intValue) {
        scrollBehavior.snapAnimationSpec
        pagerState.animateScrollToPage(selectedItem.intValue)
    }
    LaunchedEffect(pagerState.currentPage) {
        if (isClickBottomBarChange.value) {
            isClickBottomBarChange.value = false
        } else {
            selectedItem.intValue = pagerState.currentPage
        }
    }

    AppTheme {
        NavigationSuiteScaffold(selectedItem, isClickBottomBarChange) { layoutType ->
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .displayCutoutPadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .background(MaterialTheme.colorScheme.background),
                topBar = {
                    TopAppBar(scrollBehavior)
                }
            ) { padding ->
                Column(
                    Modifier.padding(top = padding.calculateTopPadding())
                ) {
                    HorizontalPager(pagerState, layoutType)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                maxLines = 1
            )
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
        actions = {
            AboutDialog()
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun NavigationSuiteScaffold(
    selectedItem: MutableState<Int>,
    isClickBottomBarChange: MutableState<Boolean>,
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
                        isClickBottomBarChange.value = true
                        selectedItem.value = index
                    }
                )
            }
        }
    ) {
        content(navLayoutType)
    }
}

@Composable
fun HorizontalPager(
    pagerState: PagerState,
    layoutType: NavigationSuiteType
) {
    HorizontalPager(
        verticalAlignment = Alignment.Top,
        state = pagerState,
        beyondViewportPageCount = 1,
        pageContent = { page ->
            when (page) {
                0 -> HomeView(layoutType)
                1 -> SansSerifView(layoutType)
                2 -> SerifView(layoutType)
            }
        }
    )
}