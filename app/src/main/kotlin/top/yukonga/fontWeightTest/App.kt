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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import top.yukonga.fontWeightTest.ui.AboutDialog
import top.yukonga.fontWeightTest.ui.HomeView
import top.yukonga.fontWeightTest.ui.SansSerifView
import top.yukonga.fontWeightTest.ui.SerifView
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
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .background(TopAppBarDefaults.topAppBarColors().containerColor)
                .displayCutoutPadding(),
            topBar = { TopAppBar(scrollBehavior) },
            bottomBar = { BottomAppBar(selectedItem, isClickBottomBarChange) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
            ) {
                HorizontalPager(pagerState)
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
fun BottomAppBar(
    selectedItem: MutableState<Int>,
    isClickBottomBarChange: MutableState<Boolean>
) {
    val labels = arrayOf(
        stringResource(R.string.home),
        stringResource(R.string.sans_serif),
        stringResource(R.string.serif)
    )
    val normalIcons = listOf(
        painterResource(R.drawable.home),
        painterResource(R.drawable.sans_serif),
        painterResource(R.drawable.serif)
    )
    val selectedIcons = listOf(
        painterResource(R.drawable.home_selected),
        painterResource(R.drawable.sans_serif_selected),
        painterResource(R.drawable.serif_selected)
    )

    NavigationBar {
        labels.forEachIndexed { index, item ->
            val icon = if (selectedItem.value == index) selectedIcons[index] else normalIcons[index]
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = item) },
                label = { Text(text = item) },
                alwaysShowLabel = false,
                selected = selectedItem.value == index,
                onClick = {
                    isClickBottomBarChange.value = true
                    selectedItem.value = index
                }
            )
        }
    }
}

@Composable
fun HorizontalPager(pagerState: PagerState) {
    HorizontalPager(
        verticalAlignment = Alignment.Top,
        state = pagerState,
        beyondViewportPageCount = 1,
        pageContent = { page ->
            when (page) {
                0 -> HomeView()
                1 -> SansSerifView()
                2 -> SerifView()
            }
        }
    )
}