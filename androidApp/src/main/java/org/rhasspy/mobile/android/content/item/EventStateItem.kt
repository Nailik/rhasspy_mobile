package org.rhasspy.mobile.android.content.item

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.RotateRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.ui.content.elements.Icon

@Composable
fun EventStateIcon(serviceState: ServiceState) {
    val rotation = if (serviceState == Loading) {
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
        imageVector = when (serviceState) {
            is Pending -> Icons.Outlined.Pending
            is Loading -> Icons.Outlined.RotateRight
            is Success -> Icons.Outlined.Done
            is Error,
            is Exception -> Icons.Filled.Error

            is Disabled -> Icons.Outlined.Circle
        },
        contentDescription = when (serviceState) {
            is Pending -> MR.strings.pending.stable
            is Loading -> MR.strings.loading.stable
            is Success -> MR.strings.success.stable
            is Error,
            is Exception -> MR.strings.error.stable

            is Disabled -> MR.strings.disabled.stable
        }
    )
}

@Composable
fun EventStateIconTinted(serviceState: ServiceState) {
    val rotation = if (serviceState == Loading) {
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
        imageVector = when (serviceState) {
            is Pending -> Icons.Outlined.Pending
            is Loading -> Icons.Outlined.RotateRight
            is Success -> Icons.Outlined.Done
            is Error,
            is Exception -> Icons.Filled.Error

            is Disabled -> Icons.Outlined.Circle
        },
        contentDescription = when (serviceState) {
            is Pending -> MR.strings.pending.stable
            is Loading -> MR.strings.loading.stable
            is Success -> MR.strings.success.stable
            is Error,
            is Exception -> MR.strings.error.stable

            is Disabled -> MR.strings.disabled.stable
        },
        tint = when (serviceState) {
            is Pending -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            is Loading -> MaterialTheme.colorScheme.secondary
            is Success -> MaterialTheme.colorScheme.primary
            is Error,
            is Exception -> MaterialTheme.colorScheme.errorContainer

            is Disabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
        }
    )
}

@Composable
fun EventStateContent(
    serviceState: ServiceState,
    content: @Composable () -> Unit
) {
    val contentColor = when (serviceState) {
        is Pending -> MaterialTheme.colorScheme.onSurfaceVariant
        is Loading -> MaterialTheme.colorScheme.onSecondaryContainer
        is Success -> MaterialTheme.colorScheme.onPrimaryContainer
        is Error,
        is Exception -> MaterialTheme.colorScheme.onErrorContainer

        is Disabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
    }

    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        content = content
    )
}

@Composable
fun EventStateCard(
    serviceState: ServiceState,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
            .let { onClick?.let { it1 -> it.clickable(enabled = enabled, onClick = it1) } ?: it },
        colors = CardDefaults.outlinedCardColors(
            containerColor = when (serviceState) {
                is Pending -> MaterialTheme.colorScheme.surfaceVariant
                is Loading -> MaterialTheme.colorScheme.secondaryContainer
                is Success -> MaterialTheme.colorScheme.primaryContainer
                is Error,
                is Exception -> MaterialTheme.colorScheme.errorContainer

                is Disabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
            }
        ),
        content = {
            EventStateContent(
                serviceState = serviceState,
                content = content
            )
        }
    )
}