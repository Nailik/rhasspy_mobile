package org.rhasspy.mobile.android.content.item

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.middleware.ServiceState

@Composable
fun EventStateIcon(serviceState: ServiceState) {
    val rotation = if (serviceState == ServiceState.Loading) {
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
            is ServiceState.Pending -> Icons.Outlined.Pending
            is ServiceState.Loading -> Icons.Outlined.RotateRight
            is ServiceState.Success -> Icons.Outlined.Done
            is ServiceState.Error,
            is ServiceState.Exception -> Icons.Filled.Error
            is ServiceState.Disabled -> Icons.Outlined.Circle
        },
        contentDescription = when (serviceState) {
            is ServiceState.Pending -> MR.strings.pending
            is ServiceState.Loading -> MR.strings.loading
            is ServiceState.Success -> MR.strings.success
            is ServiceState.Error,
            is ServiceState.Exception -> MR.strings.error
            is ServiceState.Disabled -> MR.strings.disabled
        }
    )
}

@Composable
fun EventStateIconTinted(serviceState: ServiceState) {
    val rotation = if (serviceState == ServiceState.Loading) {
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
            is ServiceState.Pending -> Icons.Outlined.Pending
            is ServiceState.Loading -> Icons.Outlined.RotateRight
            is ServiceState.Success -> Icons.Outlined.Done
            is ServiceState.Error,
            is ServiceState.Exception -> Icons.Filled.Error
            is ServiceState.Disabled -> Icons.Outlined.Circle
        },
        contentDescription = when (serviceState) {
            is ServiceState.Pending -> MR.strings.pending
            is ServiceState.Loading -> MR.strings.loading
            is ServiceState.Success -> MR.strings.success
            is ServiceState.Error,
            is ServiceState.Exception -> MR.strings.error
            is ServiceState.Disabled -> MR.strings.disabled
        },
        tint = when (serviceState) {
            is ServiceState.Pending -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            is ServiceState.Loading -> MaterialTheme.colorScheme.secondary
            is ServiceState.Success -> MaterialTheme.colorScheme.primary
            is ServiceState.Error,
            is ServiceState.Exception -> MaterialTheme.colorScheme.errorContainer
            is ServiceState.Disabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
        }
    )
}

@Composable
fun EventStateContent(
    serviceState: ServiceState,
    content: @Composable () -> Unit
) {
    val contentColor = when (serviceState) {
        is ServiceState.Pending -> MaterialTheme.colorScheme.onSurfaceVariant
        is ServiceState.Loading -> MaterialTheme.colorScheme.onSecondaryContainer
        is ServiceState.Success -> MaterialTheme.colorScheme.onPrimaryContainer
        is ServiceState.Error,
        is ServiceState.Exception -> MaterialTheme.colorScheme.onErrorContainer
        is ServiceState.Disabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
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
                is ServiceState.Pending -> MaterialTheme.colorScheme.surfaceVariant
                is ServiceState.Loading -> MaterialTheme.colorScheme.secondaryContainer
                is ServiceState.Success -> MaterialTheme.colorScheme.primaryContainer
                is ServiceState.Error,
                is ServiceState.Exception -> MaterialTheme.colorScheme.errorContainer
                is ServiceState.Disabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
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