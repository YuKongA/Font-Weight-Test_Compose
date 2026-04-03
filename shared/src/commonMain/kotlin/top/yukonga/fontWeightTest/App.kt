package top.yukonga.fontWeightTest

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import fontweighttest.shared.generated.resources.Res
import fontweighttest.shared.generated.resources.home
import fontweighttest.shared.generated.resources.monospace
import fontweighttest.shared.generated.resources.sans_serif
import fontweighttest.shared.generated.resources.serif
import fontweighttest.shared.generated.resources.tune
import fontweighttest.shared.generated.resources.unicode_nav
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import top.yukonga.fontWeightTest.ui.HomePage
import top.yukonga.fontWeightTest.ui.MonospacePage
import top.yukonga.fontWeightTest.ui.SansSerifPage
import top.yukonga.fontWeightTest.ui.SerifPage
import top.yukonga.fontWeightTest.ui.UnicodeCoveragePage
import top.yukonga.fontWeightTest.ui.theme.AppTheme
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.isRenderEffectSupported
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.abs

val LocalMainPagerState = staticCompositionLocalOf<MainPagerState> { error("LocalMainPagerState not provided") }

@Composable
fun App(
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    AppTheme(
        isDarkTheme = isDarkTheme
    ) {
        val pagerState = rememberPagerState(pageCount = { 5 })
        val mainPagerState = rememberMainPagerState(pagerState)
        LaunchedEffect(mainPagerState.pagerState.currentPage) {
            mainPagerState.syncPage()
        }

        val topAppBarScrollBehaviorList = List(5) { MiuixScrollBehavior() }

        val navigationItems = listOf(
            NavigationItem(stringResource(Res.string.home), vectorResource(Res.drawable.home)),
            NavigationItem(stringResource(Res.string.sans_serif), vectorResource(Res.drawable.sans_serif)),
            NavigationItem(stringResource(Res.string.serif), vectorResource(Res.drawable.serif)),
            NavigationItem(stringResource(Res.string.monospace), vectorResource(Res.drawable.monospace)),
            NavigationItem(stringResource(Res.string.unicode_nav), vectorResource(Res.drawable.tune)),
        )

        val backdrop = rememberLayerBackdrop()

        CompositionLocalProvider(
            LocalMainPagerState provides mainPagerState,
        ) {
            val page by remember { derivedStateOf { pagerState.targetPage } }
            val blurSupported = isRenderEffectSupported()
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        color = if (blurSupported) Color.Transparent else MiuixTheme.colorScheme.surface,
                        modifier = if (blurSupported) {
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
                    ) {
                        navigationItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = page == index,
                                onClick = { mainPagerState.animateToPage(index) },
                                icon = item.icon,
                                label = item.label,
                            )
                        }
                    }
                }
            ) { padding ->
                PagerContent(
                    pagerState = pagerState,
                    backdrop = backdrop,
                    topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                    padding = padding
                )
            }
        }
    }
}

@Composable
private fun PagerContent(
    pagerState: PagerState,
    backdrop: LayerBackdrop,
    topAppBarScrollBehaviorList: List<ScrollBehavior>,
    padding: PaddingValues
) {
    HorizontalPager(
        modifier = Modifier.layerBackdrop(backdrop),
        state = pagerState,
        pageContent = { page ->
            key(page) {
                when (page) {
                    0 -> HomePage(topAppBarScrollBehaviorList[0], padding)
                    1 -> SansSerifPage(topAppBarScrollBehaviorList[1], padding)
                    2 -> SerifPage(topAppBarScrollBehaviorList[2], padding)
                    3 -> MonospacePage(topAppBarScrollBehaviorList[3], padding)
                    4 -> UnicodeCoveragePage(topAppBarScrollBehaviorList[4], padding)
                }
            }
        }
    )
}

@Stable
class MainPagerState(
    val pagerState: PagerState,
    private val coroutineScope: CoroutineScope,
) {
    var selectedPage by mutableIntStateOf(pagerState.currentPage)
        private set

    var isNavigating by mutableStateOf(false)
        private set

    private var navJob: Job? = null

    fun animateToPage(targetIndex: Int) {
        if (targetIndex == selectedPage) return

        navJob?.cancel()

        selectedPage = targetIndex
        isNavigating = true

        navJob = coroutineScope.launch {
            val myJob = coroutineContext.job
            try {
                pagerState.scroll(MutatePriority.UserInput) {
                    val distance = abs(targetIndex - pagerState.currentPage).coerceAtLeast(2)
                    val duration = 100 * distance + 100
                    val layoutInfo = pagerState.layoutInfo
                    val pageSize = layoutInfo.pageSize + layoutInfo.pageSpacing
                    val currentDistanceInPages = targetIndex - pagerState.currentPage - pagerState.currentPageOffsetFraction
                    val scrollPixels = currentDistanceInPages * pageSize

                    var previousValue = 0f
                    animate(
                        initialValue = 0f,
                        targetValue = scrollPixels,
                        animationSpec = tween(easing = EaseInOut, durationMillis = duration),
                    ) { currentValue, _ ->
                        previousValue += scrollBy(currentValue - previousValue)
                    }
                }

                if (pagerState.currentPage != targetIndex) {
                    pagerState.scrollToPage(targetIndex)
                }
            } finally {
                if (navJob == myJob) {
                    isNavigating = false
                    if (pagerState.currentPage != targetIndex) {
                        selectedPage = pagerState.currentPage
                    }
                }
            }
        }
    }

    fun syncPage() {
        if (!isNavigating && selectedPage != pagerState.currentPage) {
            selectedPage = pagerState.currentPage
        }
    }
}

@Composable
fun rememberMainPagerState(
    pagerState: PagerState,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): MainPagerState = remember(pagerState, coroutineScope) {
    MainPagerState(pagerState, coroutineScope)
}