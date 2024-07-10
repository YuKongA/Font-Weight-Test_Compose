import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    val selectedItem = remember { mutableStateOf(0) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val offsetHeight by animateDpAsState(
        targetValue = if (scrollBehavior.state.contentOffset <= -35) 80.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .background(MaterialTheme.colorScheme.background)
                .displayCutoutPadding(),
            topBar = {
                TopAppBar(scrollBehavior)
            },
            bottomBar = {
                BottomAppBar(selectedItem, offsetHeight)
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .padding(horizontal = 20.dp)
            ) {
                item {
                    AppContent(selectedItem)
                    Spacer(Modifier.height(padding.calculateBottomPadding()))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        modifier = Modifier.displayCutoutPadding(),
        title = {
            Text(
                text = stringResource(Res.string.app_name),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                maxLines = 1,
            )
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
        actions = { AboutDialog() },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun BottomAppBar(selectedItem: MutableState<Int>, fabOffsetHeight: Dp) {
    val titleItems = listOf(
        stringResource(Res.string.home),
        stringResource(Res.string.sans_serif),
        stringResource(Res.string.serif),
    )
    val iconItems = listOf(
        painterResource(Res.drawable.home),
        painterResource(Res.drawable.sans_serif),
        painterResource(Res.drawable.serif)
    )
    val selectedIconItems = listOf(
        painterResource(Res.drawable.home_selected),
        painterResource(Res.drawable.sans_serif_selected),
        painterResource(Res.drawable.serif_selected)
    )

    NavigationBar(
        modifier = Modifier.offset(y = fabOffsetHeight)
    ) {
        titleItems.forEachIndexed { index, item ->
            val icon = if (selectedItem.value == index) selectedIconItems[index] else iconItems[index]
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = item) },
                label = {
                    if (selectedItem.value == index) {
                        Text(text = item)
                    }
                },
                selected = selectedItem.value == index,
                onClick = { selectedItem.value = index }
            )
        }
    }
}


@Composable
fun AppContent(selectedItem: MutableState<Int>) {
    when (selectedItem.value) {
        0 -> HomeContent()
        1 -> SansSerifContent()
        2 -> SerifContent()
        else -> HomeContent()
    }
}


@Composable
fun HomeContent() {
    HomeView()
}

@Composable
fun SansSerifContent() {
    SansSerifView()
}

@Composable
fun SerifContent() {
    SerifView()
}