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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import top.yukonga.fontWeightTest.BuildConfig
import top.yukonga.fontWeightTest.R
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog

@Composable
fun AboutDialog() {
    val showDialog = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    Surface(
        modifier = Modifier
            .padding(start = 26.dp)
            .clickable(
                indication = null,
                interactionSource = null,
                onClick = {
                    showDialog.value = true
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            ),
        color = Color.Transparent
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(R.drawable.icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
        )
    }

    SuperDialog(
        show = showDialog,
        title = "About",
        onDismissRequest = { dismissDialog(showDialog) },
        content = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(MiuixTheme.colorScheme.primary)
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon),
                        colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onPrimary),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                    )
                }
                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                    )
                }
            }
            Column(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                val uriHandler = LocalUriHandler.current
                Row {
                    Text(
                        text = stringResource(R.string.view_source) + " "
                    )
                    Text(
                        text = AnnotatedString(
                            text = "GitHub",
                            spanStyle = SpanStyle(textDecoration = TextDecoration.Underline, color = MiuixTheme.colorScheme.primary)
                        ),
                        modifier = Modifier.clickable(
                            onClick = {
                                uriHandler.openUri("https://github.com/YuKongA/Font-Weight-Test_Compose")
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                    )
                }
                Row {
                    Text(
                        text = stringResource(R.string.join_group) + " "
                    )
                    Text(
                        text = AnnotatedString(
                            text = "Telegram",
                            spanStyle = SpanStyle(textDecoration = TextDecoration.Underline, color = MiuixTheme.colorScheme.primary)
                        ),
                        modifier = Modifier.clickable(
                            onClick = {
                                uriHandler.openUri("https://t.me/YuKongA13579")
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                        )
                    )
                }
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Text(
                    text = stringResource(R.string.opensource_info)
                )
            }
        }
    )
}