package top.yukonga.fontWeightTest

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.fontWeightTest.misc.NavigationItem
import top.yukonga.fontWeightTest.misc.navigationItems
import top.yukonga.fontWeightTest.ui.AboutDialog
import top.yukonga.fontWeightTest.ui.HomeView
import top.yukonga.fontWeightTest.ui.SansSerifView
import top.yukonga.fontWeightTest.ui.SerifView
import top.yukonga.fontWeightTest.ui.components.NavigationRailBottom
import top.yukonga.fontWeightTest.ui.theme.AppTheme

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun App() {
    val pagerState = rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f, pageCount = { 3 })
    val selectedItem = remember { mutableIntStateOf(0) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
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
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .background(TopAppBarDefaults.topAppBarColors().containerColor)
                .displayCutoutPadding(),
            topBar = {
                val orientation = LocalConfiguration.current.orientation
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    TopAppBar(scrollBehavior)
                }
            },
            bottomBar = {
                val orientation = LocalConfiguration.current.orientation
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    NavigationBarView(selectedItem, isClickBottomBarChange)
                }
            }
        ) { padding ->
            val orientation = LocalConfiguration.current.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Row {
                    NavigationRailView(selectedItem, isClickBottomBarChange)
                    Column {
                        TopAppBar(scrollBehavior)
                        HorizontalPager(pagerState, padding.calculateBottomPadding())
                    }
                }
            } else {
                Column(
                    Modifier.padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
                ) {
                    HorizontalPager(pagerState)
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
            containerColor = TopAppBarDefaults.topAppBarColors().containerColor,
            scrolledContainerColor = TopAppBarDefaults.topAppBarColors().containerColor,
            actionIconContentColor = TopAppBarDefaults.topAppBarColors().actionIconContentColor,
            navigationIconContentColor = TopAppBarDefaults.topAppBarColors().navigationIconContentColor,
            titleContentColor = TopAppBarDefaults.topAppBarColors().titleContentColor,
        ),
        actions = { AboutDialog() },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun NavigationItemsView(
    items: List<NavigationItem>,
    selectedItem: MutableState<Int>,
    isClickBottomBarChange: MutableState<Boolean>,
    itemContent: @Composable (NavigationItem, Boolean, () -> Unit) -> Unit
) {
    items.forEachIndexed { index, item ->
        val isSelected = selectedItem.value == index
        itemContent(item, isSelected) {
            isClickBottomBarChange.value = true
            selectedItem.value = index
        }
    }
}

@Composable
fun NavigationRailView(
    selectedItem: MutableState<Int>,
    isClickBottomBarChange: MutableState<Boolean>
) {
    NavigationRailBottom {
        NavigationItemsView(navigationItems, selectedItem, isClickBottomBarChange) { item, isSelected, onClick ->
            NavigationRailItem(
                icon = { Icon(painterResource(if (isSelected) item.selectedIcon else item.normalIcon), contentDescription = stringResource(item.label)) },
                label = { Text(text = stringResource(item.label)) },
                alwaysShowLabel = false,
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

@Composable
fun NavigationBarView(
    selectedItem: MutableState<Int>,
    isClickBottomBarChange: MutableState<Boolean>
) {
    NavigationBar {
        NavigationItemsView(navigationItems, selectedItem, isClickBottomBarChange) { item, isSelected, onClick ->
            NavigationBarItem(
                icon = { Icon(painterResource(if (isSelected) item.selectedIcon else item.normalIcon), contentDescription = stringResource(item.label)) },
                label = { Text(text = stringResource(item.label)) },
                alwaysShowLabel = false,
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

@Composable
fun HorizontalPager(pagerState: PagerState, calculateBottomPadding: Dp = 0.dp) {
    HorizontalPager(
        verticalAlignment = Alignment.Top,
        state = pagerState,
        beyondViewportPageCount = 1,
        pageContent = { page ->
            when (page) {
                0 -> HomeView(calculateBottomPadding)
                1 -> SansSerifView(calculateBottomPadding)
                2 -> SerifView(calculateBottomPadding)

            }
        }
    )
}