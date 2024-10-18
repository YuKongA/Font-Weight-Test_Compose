package top.yukonga.fontWeightTest.utils

import androidx.compose.ui.text.font.FontWeight
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import top.yukonga.fontWeightTest.R

val fontWeightList = listOf(
    FontWeight.Thin, // W100
    FontWeight.ExtraLight, // W200
    FontWeight.Light, // W300
    FontWeight.Normal, // W400
    FontWeight.Medium, // W500
    FontWeight.SemiBold, // W600
    FontWeight.Bold, // W700
    FontWeight.ExtraBold, // W800
    FontWeight.Black // W900
)

val miSansList = listOf(
    R.font.misans_thin, // W100
    R.font.misans_extralight, // W200
    R.font.misans_light, // W300
    R.font.misans_normal, // W400
    R.font.misans_medium, // W500
    R.font.misans_semibold, // W600
    R.font.misans_bold, // W700
    R.font.misans_extrabold, // W800
    R.font.misans_black // W900
)

val navigationItems = listOf(
    NavigationItem(R.string.home, R.drawable.home, R.drawable.home_selected),
    NavigationItem(R.string.sans_serif, R.drawable.sans_serif, R.drawable.sans_serif_selected),
    NavigationItem(R.string.serif, R.drawable.serif, R.drawable.serif_selected),
    NavigationItem(R.string.monospace, R.drawable.sans_serif, R.drawable.sans_serif_selected),
)

data class NavigationItem(
    val label: Int,
    val normalIcon: Int,
    val selectedIcon: Int
)

fun WindowSizeClass.isCompact() = windowWidthSizeClass == WindowWidthSizeClass.COMPACT || windowHeightSizeClass == WindowHeightSizeClass.COMPACT
