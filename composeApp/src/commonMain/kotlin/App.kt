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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import fontweighttest.composeapp.generated.resources.Res
import fontweighttest.composeapp.generated.resources.app_name
import fontweighttest.composeapp.generated.resources.home
import fontweighttest.composeapp.generated.resources.home_selected
import fontweighttest.composeapp.generated.resources.sans_serif
import fontweighttest.composeapp.generated.resources.sans_serif_selected
import fontweighttest.composeapp.generated.resources.serif
import fontweighttest.composeapp.generated.resources.serif_selected
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ui.AboutDialog
import ui.HomeView
import ui.SansSerifView
import ui.SerifView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val pagerState = rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f, pageCount = { 3 })
    val selectedItem = remember { mutableStateOf(0) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val isClickBottomBarChange = remember { mutableStateOf(false) }

    LaunchedEffect(selectedItem.value) {
        pagerState.animateScrollToPage(selectedItem.value)
    }
    LaunchedEffect(pagerState.currentPage) {
        if (isClickBottomBarChange.value) {
            isClickBottomBarChange.value = false
        } else {
            selectedItem.value = pagerState.currentPage
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(Res.string.app_name),
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
        stringResource(Res.string.home),
        stringResource(Res.string.sans_serif),
        stringResource(Res.string.serif)
    )
    val normalIcons = listOf(
        painterResource(Res.drawable.home),
        painterResource(Res.drawable.sans_serif),
        painterResource(Res.drawable.serif)
    )
    val selectedIcons = listOf(
        painterResource(Res.drawable.home_selected),
        painterResource(Res.drawable.sans_serif_selected),
        painterResource(Res.drawable.serif_selected)
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