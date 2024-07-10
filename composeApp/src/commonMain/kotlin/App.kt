import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fontweighttest.composeapp.generated.resources.Res
import fontweighttest.composeapp.generated.resources.app_name
import fontweighttest.composeapp.generated.resources.home
import fontweighttest.composeapp.generated.resources.home_selected
import fontweighttest.composeapp.generated.resources.sans_serif
import fontweighttest.composeapp.generated.resources.sans_serif_selected
import fontweighttest.composeapp.generated.resources.serif
import fontweighttest.composeapp.generated.resources.serif_selected
import misc.RouteConfig
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ui.AboutDialog
import ui.HomeView
import ui.SansSerifView
import ui.SerifView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    AppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .background(TopAppBarDefaults.topAppBarColors().containerColor)
                .displayCutoutPadding(),
            topBar = { TopAppBar(scrollBehavior) },
            bottomBar = { BottomAppBar(navController) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
                    .padding(horizontal = 20.dp)
            ) {
                setupNavHost(navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
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
fun BottomAppBar(navController: NavHostController) {
    val routes = listOf(
        RouteConfig.HOME,
        RouteConfig.SANS_SERIF,
        RouteConfig.SERIF
    )
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        routes.forEachIndexed { index, route ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == route } ?: false
            val currentIcon = if (isSelected) selectedIcons[index] else normalIcons[index]
            NavigationBarItem(
                label = { Text(labels[index]) },
                icon = { Icon(currentIcon, contentDescription = labels[index]) },
                selected = isSelected,
                alwaysShowLabel = false,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().route!!) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun setupNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = RouteConfig.HOME,
    ) {
        composable(route = RouteConfig.HOME) { HomeView() }
        composable(route = RouteConfig.SANS_SERIF) { SansSerifView() }
        composable(route = RouteConfig.SERIF) { SerifView() }
    }
}