package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import fontweighttest.composeapp.generated.resources.app_name
import fontweighttest.composeapp.generated.resources.icon
import fontweighttest.composeapp.generated.resources.join_group
import fontweighttest.composeapp.generated.resources.opensource_info
import fontweighttest.composeapp.generated.resources.view_source
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

const val version = "v1.1.0"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDialog() {
    var showDialog by remember { mutableStateOf(false) }

    val hapticFeedback = LocalHapticFeedback.current

    IconButton(
        modifier = Modifier.widthIn(max = 48.dp),
        onClick = {
            showDialog = true
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = vectorResource(Res.drawable.icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = { showDialog = false },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(280.dp, 190.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Row(modifier = Modifier.padding(24.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Image(
                                imageVector = vectorResource(Res.drawable.icon),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                                contentDescription = null,
                                modifier = Modifier.size(25.dp),
                            )
                        }
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.app_name),
                                modifier = Modifier,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                            Text(
                                text = version,
                                modifier = Modifier,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 88.dp)
                    ) {
                        val uriHandler = LocalUriHandler.current
                        Row {
                            Text(
                                text = stringResource(Res.string.view_source) + " ",
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            )
                            Text(
                                text = AnnotatedString(
                                    text = "GitHub",
                                    spanStyle = SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.primary)
                                ),
                                modifier = Modifier.clickable(
                                    onClick = {
                                        uriHandler.openUri("https://github.com/YuKongA/Font_Weight_Test-KMP")
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                )
                            )
                        }
                        Row {
                            Text(
                                text = stringResource(Res.string.join_group) + " ",
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            )
                            Text(
                                text = AnnotatedString(
                                    text = "Telegram",
                                    spanStyle = SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.primary)
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
                            text = stringResource(Res.string.opensource_info),
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    }
                }
            }
        )
    }
}