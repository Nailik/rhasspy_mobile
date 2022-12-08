package org.rhasspy.mobile.android.content

import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
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
import org.rhasspy.mobile.android.theme.on_color_warn
import org.rhasspy.mobile.android.theme.warn
import org.rhasspy.mobile.android.utils.Icon
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
            is EventState.Error -> Icons.Outlined.ErrorOutline
        },
        contentDescription = when (eventState) {
            is EventState.Pending -> MR.strings.pending
            is EventState.Loading -> MR.strings.loading
            is EventState.Success -> MR.strings.success
            is EventState.Warning -> MR.strings.warning
            is EventState.Error -> MR.strings.error
        }
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
            }
        ),
        onClick = onClick,
        content = {
            val contentColor = when (eventState) {
                is EventState.Pending -> MaterialTheme.colorScheme.onSurfaceVariant
                is EventState.Loading -> MaterialTheme.colorScheme.onSecondaryContainer
                is EventState.Success -> MaterialTheme.colorScheme.onPrimaryContainer
                is EventState.Warning -> MaterialTheme.colorScheme.on_color_warn
                is EventState.Error -> MaterialTheme.colorScheme.onErrorContainer
            }

            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                content = content
            )
        }
    )
}