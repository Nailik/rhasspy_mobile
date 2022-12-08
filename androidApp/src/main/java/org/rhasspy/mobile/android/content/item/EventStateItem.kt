package org.rhasspy.mobile.android.content.item

import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.theme.on_color_warn
import org.rhasspy.mobile.android.theme.warn
import org.rhasspy.mobile.middleware.EventState

@Composable
fun EventStateIcon(eventState: EventState) {
    val rotation = if (eventState == EventState.Loading) {
        val infiniteTransition = rememberInfiniteTransition()
        val animateRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 359f, animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Restart
            )
        )
        animateRotation
    } else {
        0f
    }

    Icon(
        modifier = Modifier.rotate(rotation),
        imageVector = when (eventState) {
            is EventState.Pending -> Icons.Outlined.Pending
            is EventState.Loading -> Icons.Outlined.RotateRight
            is EventState.Success -> Icons.Outlined.Done
            is EventState.Warning -> Icons.Outlined.Warning
            is EventState.Error -> Icons.Filled.Error
            is EventState.Disabled -> Icons.Outlined.Circle
        },
        contentDescription = when (eventState) {
            is EventState.Pending -> MR.strings.pending
            is EventState.Loading -> MR.strings.loading
            is EventState.Success -> MR.strings.success
            is EventState.Warning -> MR.strings.warning
            is EventState.Error -> MR.strings.error
            is EventState.Disabled -> MR.strings.disabled
        }
    )
}

@Composable
fun EventStateIconTinted(eventState: EventState) {
    val rotation = if (eventState == EventState.Loading) {
        val infiniteTransition = rememberInfiniteTransition()
        val animateRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 359f, animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Restart
            )
        )
        animateRotation
    } else {
        0f
    }

    Icon(
        modifier = Modifier.rotate(rotation),
        imageVector = when (eventState) {
            is EventState.Pending -> Icons.Outlined.Pending
            is EventState.Loading -> Icons.Outlined.RotateRight
            is EventState.Success -> Icons.Outlined.Done
            is EventState.Warning -> Icons.Outlined.Warning
            is EventState.Error -> Icons.Filled.Error
            is EventState.Disabled -> Icons.Outlined.Circle
        },
        contentDescription = when (eventState) {
            is EventState.Pending -> MR.strings.pending
            is EventState.Loading -> MR.strings.loading
            is EventState.Success -> MR.strings.success
            is EventState.Warning -> MR.strings.warning
            is EventState.Error -> MR.strings.error
            is EventState.Disabled -> MR.strings.disabled
        },
        tint = when (eventState) {
            is EventState.Pending -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            is EventState.Loading -> MaterialTheme.colorScheme.secondary
            is EventState.Success -> MaterialTheme.colorScheme.primary
            is EventState.Warning -> MaterialTheme.colorScheme.warn
            is EventState.Error -> MaterialTheme.colorScheme.errorContainer
            is EventState.Disabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        }
    )
}

@Composable
fun EventStateContent(
    eventState: EventState,
    content: @Composable () -> Unit
) {
    val contentColor = when (eventState) {
        is EventState.Pending -> MaterialTheme.colorScheme.onSurfaceVariant
        is EventState.Loading -> MaterialTheme.colorScheme.onSecondaryContainer
        is EventState.Success -> MaterialTheme.colorScheme.onPrimaryContainer
        is EventState.Warning -> MaterialTheme.colorScheme.on_color_warn
        is EventState.Error -> MaterialTheme.colorScheme.onErrorContainer
        is EventState.Disabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }

    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        content = content
    )
}

@Composable
fun EventStateCard(
    eventState: EventState,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.outlinedCardColors(
            containerColor = when (eventState) {
                is EventState.Pending -> MaterialTheme.colorScheme.surfaceVariant
                is EventState.Loading -> MaterialTheme.colorScheme.secondaryContainer
                is EventState.Success -> MaterialTheme.colorScheme.primaryContainer
                is EventState.Warning -> MaterialTheme.colorScheme.warn
                is EventState.Error -> MaterialTheme.colorScheme.errorContainer
                is EventState.Disabled -> MaterialTheme.colorScheme.surface.copy(alpha = 0.38f)
            }
        ),
        onClick = onClick,
        content = {
            EventStateContent(
                eventState = eventState,
                content = content
            )
        }
    )
}