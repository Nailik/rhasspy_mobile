package org.rhasspy.mobile.ui.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.data.service.ConnectionState.*
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.item.EventStateIcon
import org.rhasspy.mobile.ui.content.list.ListElement

@Composable
fun ConnectionStateHeaderItem(
    connectionStateFlow: StateFlow<ConnectionState>,
) {
    val connectionState by connectionStateFlow.collectAsState()

    val contentColor = when (connectionState) {
        is Success    -> MaterialTheme.colorScheme.onPrimaryContainer
        is ErrorState -> MaterialTheme.colorScheme.onErrorContainer
        is Disabled   -> MaterialTheme.colorScheme.onSecondaryContainer
        is Loading    -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    AnimatedContent(connectionState) { state ->
        ListElement(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = ListItemDefaults.colors(
                containerColor = when (state) {
                    is Success    -> MaterialTheme.colorScheme.primaryContainer
                    is ErrorState -> MaterialTheme.colorScheme.errorContainer
                    is Disabled   -> MaterialTheme.colorScheme.secondaryContainer
                    is Loading    -> MaterialTheme.colorScheme.surfaceVariant
                },
                headlineColor = contentColor,
                leadingIconColor = contentColor,
                supportingColor = contentColor,
            ),
            icon = { EventStateIcon(state) },
            text = { ConnectionStateText(state) },
            secondaryText = {
                when (state) {
                    is ErrorState -> Text(state.message, maxLines = 2, minLines = 2)
                    is Disabled   -> Text(MR.strings.disabled.stable, maxLines = 2, minLines = 2)
                    is Loading    -> Text(MR.strings.loading.stable, maxLines = 2, minLines = 2)
                    is Success    -> Text(MR.strings.success.stable, maxLines = 2, minLines = 2)
                }
            }
        )
    }

}

/**
 * text of service state
 */
@Composable
private fun ConnectionStateText(connectionState: ConnectionState) {

    Text(
        resource = when (connectionState) {
            is Success    -> MR.strings.success.stable
            is ErrorState -> MR.strings.error.stable
            is Disabled   -> MR.strings.disabled.stable
            is Loading    -> MR.strings.loading.stable
        }
    )

}