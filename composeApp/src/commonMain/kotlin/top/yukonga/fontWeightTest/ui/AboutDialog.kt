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
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape

@Composable
fun AboutDialog() {
    val showDialog = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    IconButton(
        modifier = Modifier.padding(start = 18.dp),
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
        onDismissRequest = { showDialog.value = false },
        content = {
            AboutDialogContent(
                onLinkClick = { url ->
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                }
            )
        }
    )
}

@Composable
private fun AboutDialogContent(
    onLinkClick: (String) -> Unit
) {
    val versionInfo = remember {
        "${VersionInfo.VERSION_NAME} (${VersionInfo.VERSION_CODE})"
    }

    Column {
        AppInfoSection(versionInfo = versionInfo)
        LinksSection(onLinkClick = onLinkClick)
        OpenSourceSection()
    }
}

@Composable
private fun AppInfoSection(versionInfo: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon()
        AppDetails(versionInfo = versionInfo)
    }
}

@Composable
private fun AppIcon() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(G2RoundedCornerShape(12.dp))
            .background(MiuixTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(Res.drawable.icon),
            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onPrimary),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
        )
    }
}

@Composable
private fun AppDetails(versionInfo: String) {
    Column {
        Text(
            text = stringResource(Res.string.app_name),
            fontWeight = FontWeight.SemiBold
        )
        Text(text = versionInfo)
    }
}

@Composable
private fun LinksSection(onLinkClick: (String) -> Unit) {
    Column(
        modifier = Modifier.padding(top = 12.dp)
    ) {
        val uriHandler = LocalUriHandler.current

        LinkRow(
            prefixText = stringResource(Res.string.view_source),
            linkText = "GitHub",
            url = "https://github.com/YuKongA/Font-Weight-Test_Compose",
            onClick = { url ->
                uriHandler.openUri(url)
                onLinkClick(url)
            }
        )

        LinkRow(
            prefixText = stringResource(Res.string.join_group),
            linkText = "Telegram",
            url = "https://t.me/YuKongA13579",
            onClick = { url ->
                uriHandler.openUri(url)
                onLinkClick(url)
            }
        )
    }
}

@Composable
private fun LinkRow(
    prefixText: String,
    linkText: String,
    url: String,
    onClick: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$prefixText ")

        val primaryColor = MiuixTheme.colorScheme.primary
        val annotatedLinkText = remember(linkText, primaryColor) {
            AnnotatedString(
                text = linkText,
                spanStyle = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = primaryColor
                )
            )
        }

        Text(
            text = annotatedLinkText,
            modifier = Modifier.clickable { onClick(url) }
        )
    }
}

@Composable
private fun OpenSourceSection() {
    Column {
        Spacer(modifier = Modifier.padding(top = 12.dp))
        Text(text = stringResource(Res.string.opensource_info))
    }
}
