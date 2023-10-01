package org.rhasspy.mobile.ui.content

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntSize
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

    AnimatedVisibility(
        visible = connectionState !is Disabled,
        enter = fadeIn() + expandIn(
            expandFrom = Alignment.TopCenter,
            initialSize = { fullSize -> IntSize(fullSize.width, 0) },
        ),
        exit = shrinkOut(
            shrinkTowards = Alignment.TopCenter,
            targetSize = { fullSize -> IntSize(fullSize.width, 0) }
        ) + fadeOut(),
    ) {
        val contentColor = when (connectionState) {
            is Success    -> MaterialTheme.colorScheme.onPrimaryContainer
            is ErrorState -> MaterialTheme.colorScheme.onErrorContainer
            is Disabled   -> return@AnimatedVisibility
        }

        ListElement(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = ListItemDefaults.colors(
                containerColor = when (connectionState) {
                    is Success    -> MaterialTheme.colorScheme.primaryContainer
                    is ErrorState -> MaterialTheme.colorScheme.errorContainer
                    is Disabled   -> return@AnimatedVisibility
                },
                headlineColor = contentColor,
                leadingIconColor = contentColor,
                supportingColor = contentColor,
            ),
            icon = { EventStateIcon(connectionState) },
            text = { ConnectionStateText(connectionState) },
            secondaryText = {
                when (val state = connectionState) {
                    is ErrorState -> Text(state.getText())
                    else          -> Unit
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
            Disabled      -> return
        }
    )

}