package org.rhasspy.mobile.ui.content.item

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DomainDisabled
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
            is Disabled   -> Icons.Filled.DomainDisabled
            is Loading    -> Icons.Filled.Pending
        },
        contentDescription = when (connectionState) {
            is Success    -> MR.strings.success.stable
            is ErrorState -> MR.strings.error.stable
            is Disabled   -> MR.strings.disabled.stable
            is Loading    -> MR.strings.loading.stable
        }
    )
}

@Composable
fun EventStateIconTinted(connectionState: ConnectionState) {
    Icon(
        imageVector = when (connectionState) {
            is Success    -> Icons.Outlined.Done
            is ErrorState -> Icons.Filled.Error
            is Disabled   -> Icons.Filled.DomainDisabled
            is Loading    -> Icons.Filled.Pending
        },
        contentDescription = when (connectionState) {
            is Success    -> MR.strings.success.stable
            is ErrorState -> MR.strings.error.stable
            is Disabled   -> MR.strings.disabled.stable
            is Loading    -> MR.strings.loading.stable
        },
        tint = when (connectionState) {
            is Success    -> MaterialTheme.colorScheme.primary
            is ErrorState -> MaterialTheme.colorScheme.errorContainer
            is Disabled   -> MaterialTheme.colorScheme.secondaryContainer
            is Loading    -> MaterialTheme.colorScheme.surfaceVariant
        }
    )
}