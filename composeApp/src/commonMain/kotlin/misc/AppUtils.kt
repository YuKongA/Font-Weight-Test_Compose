package misc

import androidx.compose.ui.text.font.FontWeight


object RouteConfig {
    const val HOME = "home"
    const val SANS_SERIF = "sans_serif"
    const val SERIF = "serif"
}

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
