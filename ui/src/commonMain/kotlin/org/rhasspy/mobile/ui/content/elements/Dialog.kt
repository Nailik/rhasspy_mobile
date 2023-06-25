package org.rhasspy.mobile.ui.content.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.testTag

@Composable
fun Dialog(
    testTag: TestTag,
    icon: ImageVector? = null,
    title: StableStringResource? = null,
    message: Any,
    confirmLabel: StableStringResource,
    dismissLabel: StableStringResource? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onClose: () -> Unit = onDismiss //outside click
) {

    Dialog(
        testTag = testTag,
        icon = icon,
        title = title,
        supportingText = {
            when (message) {
                is StableStringResource -> Text(message)
                is String -> Text(message)
                else -> Text(message.toString())
            }
        },
        confirmLabel = confirmLabel,
        dismissLabel = dismissLabel,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        onClose = onClose
    )

}

@Composable
fun Dialog(
    testTag: TestTag,
    icon: ImageVector? = null,
    title: StableStringResource? = null,
    supportingText: (@Composable () -> Unit),
    confirmLabel: StableStringResource,
    dismissLabel: StableStringResource? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onClose: () -> Unit = onDismiss //outside click
) {
    Dialog(
        modifier = Modifier.testTag(testTag),
        onDismissRequest = onClose,
        headline = title?.let { { Text(it) } },
        supportingText = supportingText,
        icon =
        icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = MR.strings.icon.stable
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = dismissLabel?.let {
            {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag(TestTag.DialogCancel)
                ) {
                    Text(it)
                }
            }
        }
    )
}

@Composable
private fun Dialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    icon: (@Composable () -> Unit)? = null,
    headline: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    confirmButton: @Composable () -> Unit,
    dismissButton: (@Composable () -> Unit)? = null,
    dismissOnOutside: Boolean = true,
    showDivider: Boolean = false
) {
    DialogContainer {

        //Scrim
        Surface(
            modifier = modifier
                .fillMaxSize()
                .clickable(enabled = dismissOnOutside, onClick = onDismissRequest),
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)
        ) {

            //Container
            Surface(
                modifier = Modifier
                    .widthIn(min = 280.dp, max = 560.dp)
                    .padding(48.dp)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(28.dp))
                    .clickable(enabled = false) { },
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    Column(
                        modifier = Modifier
                            .weight(weight = 1f, fill = false)
                    ) {
                        if (icon != null) {
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 16.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Box(
                                    modifier = Modifier.size(24.dp),
                                ) {
                                    CompositionLocalProvider(
                                        LocalContentColor provides MaterialTheme.colorScheme.secondary,
                                    ) {
                                        //Icon (optional)
                                        icon()
                                    }
                                }
                            }
                        }

                        if(headline != null) {
                            CompositionLocalProvider(
                                LocalContentColor provides MaterialTheme.colorScheme.onSurface,
                                LocalTextStyle provides MaterialTheme.typography.headlineSmall
                            ) {
                                //Headline
                                headline()
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        if (supportingText != null) {
                            CompositionLocalProvider(
                                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                                LocalTextStyle provides MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Start)
                            ) {
                                //Supporting text
                                supportingText()
                            }
                        }

                        //Divider (optional)
                        if (showDivider) {
                            Divider()
                        }
                    }


                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.primary,
                        LocalTextStyle provides MaterialTheme.typography.labelLarge.copy(textAlign = TextAlign.Start)
                    ) {
                        //Text button
                        Row(
                            modifier = Modifier.padding(top = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))

                            //Dismiss
                            dismissButton?.invoke()

                            //Confirm
                            confirmButton()
                        }
                    }

                }

            }
        }

    }

}