package top.yukonga.fontWeightTest.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import fontweighttest.composeapp.generated.resources.Res
import fontweighttest.composeapp.generated.resources.about
import fontweighttest.composeapp.generated.resources.app_name
import fontweighttest.composeapp.generated.resources.icon
import fontweighttest.composeapp.generated.resources.join_group
import fontweighttest.composeapp.generated.resources.opensource_info
import fontweighttest.composeapp.generated.resources.view_source
import misc.VersionInfo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun AboutDialog() {
    val showDialog = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    IconButton(
        modifier = Modifier
            .padding(start = 18.dp),
        onClick = {
            showDialog.value = true
            hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
        },
        holdDownState = showDialog.value
    ) {
        Image(
            modifier = Modifier.size(30.dp),
            painter = painterResource(Res.drawable.icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
        )
    }

    SuperDialog(
        show = showDialog,
        title = stringResource(Res.string.about),
        onDismissRequest = {
            showDialog.value = false
        },
        content = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(SmoothRoundedCornerShape(12.dp))
                        .background(MiuixTheme.colorScheme.primary)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.icon),
                        colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onPrimary),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                    )
                }
                Column {
                    Text(
                        text = stringResource(Res.string.app_name),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = VersionInfo.VERSION_NAME + " (" + VersionInfo.VERSION_CODE + ")",
                    )
                }
            }
            Column(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                val uriHandler = LocalUriHandler.current
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.view_source) + " "
                    )
                    Text(
                        text = AnnotatedString(
                            text = "GitHub",
                            spanStyle = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = MiuixTheme.colorScheme.primary
                            )
                        ),
                        modifier = Modifier.clickable(
                            onClick = {
                                uriHandler.openUri("https://github.com/YuKongA/Font-Weight-Test_Compose")
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                            }
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.join_group) + " "
                    )
                    Text(
                        text = AnnotatedString(
                            text = "Telegram",
                            spanStyle = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = MiuixTheme.colorScheme.primary
                            )
                        ),
                        modifier = Modifier.clickable(
                            onClick = {
                                uriHandler.openUri("https://t.me/YuKongA13579")
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                            },
                        )
                    )
                }
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Text(
                    text = stringResource(Res.string.opensource_info)
                )
            }
        }
    )
}