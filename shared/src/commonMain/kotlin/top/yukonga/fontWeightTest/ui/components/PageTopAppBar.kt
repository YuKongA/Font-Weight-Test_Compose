package top.yukonga.fontWeightTest.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import top.yukonga.fontWeightTest.ui.AboutDialog
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.Backdrop
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.isRenderEffectSupported
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun PageTopAppBar(
    title: String,
    scrollBehavior: ScrollBehavior,
    backdrop: Backdrop
) {
    val blurSupported = isRenderEffectSupported()
    val topBarColor = if (blurSupported) Color.Transparent else MiuixTheme.colorScheme.surface
    BoxWithConstraints {
        val isCompact = maxWidth < 768.dp
        val modifier = if (blurSupported) {
            Modifier
                .textureBlur(
                    backdrop = backdrop,
                    blurRadius = 25f * LocalDensity.current.density,
                    shape = RectangleShape,
                    colors = BlurColors(
                        blendColors = listOf(
                            BlendColorEntry(color = MiuixTheme.colorScheme.surface.copy(0.8f))
                        )
                    )
                )
        } else Modifier
        if (isCompact) {
            TopAppBar(
                color = topBarColor,
                modifier = modifier,
                title = title,
                navigationIcon = { AboutDialog() },
                scrollBehavior = scrollBehavior
            )
        } else {
            SmallTopAppBar(
                color = topBarColor,
                modifier = modifier,
                title = title,
                navigationIcon = { AboutDialog() },
                scrollBehavior = scrollBehavior
            )
        }
    }
}
