package org.rhasspy.mobile.ui.content.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.data.service.ConnectionState.*
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.elements.Icon

@Composable
fun EventStateIcon(connectionState: ConnectionState) {
    Icon(
        imageVector = when (connectionState) {
            is Success    -> Icons.Outlined.Done
            is ErrorState -> Icons.Filled.Error
            is Unknown   -> return
        },
        contentDescription = when (connectionState) {
            is Success    -> MR.strings.success.stable
            is ErrorState -> MR.strings.error.stable
            is Unknown   -> return
        }
    )
}

@Composable
fun EventStateIconTinted(connectionState: ConnectionState) {
    Icon(
        imageVector = when (connectionState) {
            is Success    -> Icons.Outlined.Done
            is ErrorState -> Icons.Filled.Error
            is Unknown    -> return
        },
        contentDescription = when (connectionState) {
            is Success    -> MR.strings.success.stable
            is ErrorState -> MR.strings.error.stable
            is Unknown    -> return
        },
        tint = when (connectionState) {
            is Success    -> MaterialTheme.colorScheme.primary
            is ErrorState -> MaterialTheme.colorScheme.errorContainer
            is Unknown    -> return
        }
    )
}

@Composable
fun EventStateContent(
    connectionState: ConnectionState,
    content: @Composable () -> Unit
) {
    val contentColor = when (connectionState) {
        is Success    -> MaterialTheme.colorScheme.onPrimaryContainer
        is ErrorState -> MaterialTheme.colorScheme.onErrorContainer
        is Unknown   -> return
    }

    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        content = content
    )
}

@Composable
fun EventStateCard(
    connectionState: ConnectionState,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
            .let { onClick?.let { it1 -> it.clickable(enabled = enabled, onClick = it1) } ?: it },
        colors = CardDefaults.outlinedCardColors(
            containerColor = when (connectionState) {
                is Success    -> MaterialTheme.colorScheme.primaryContainer
                is ErrorState -> MaterialTheme.colorScheme.errorContainer
                is Unknown   -> return
            }
        ),
        content = {
            EventStateContent(
                connectionState = connectionState,
                content = content
            )
        }
    )
}