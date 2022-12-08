package org.rhasspy.mobile.android.content.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.item.EventStateIcon
import org.rhasspy.mobile.android.theme.warn
import org.rhasspy.mobile.middleware.Event
import org.rhasspy.mobile.middleware.EventState
import org.rhasspy.mobile.middleware.name

//loading, positive, negative state, text
@Composable
fun EventListItem(event: Event) {

    val state by event.eventState.collectAsState()

    val contentColor = when (state) {
        EventState.Pending -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        EventState.Loading -> LocalContentColor.current
        is EventState.Success -> MaterialTheme.colorScheme.primary
        is EventState.Warning -> MaterialTheme.colorScheme.warn
        is EventState.Error -> MaterialTheme.colorScheme.error
    }

    ListItem(
        leadingContent = { EventStateIcon(state) },
        colors = ListItemDefaults.colors(
            leadingIconColor = contentColor,
            headlineColor = contentColor
        ),
        overlineText = {
            Text(event.eventType.name)
        },
        supportingText = {
            val text = when (state) {
                EventState.Pending -> translate(MR.strings.pending)
                EventState.Loading -> translate(MR.strings.loading)
                is EventState.Success -> state.information
                is EventState.Warning -> state.information
                is EventState.Error -> state.toString()
            }

            Text(text ?: translate(MR.strings.noDetails))
        },
        headlineText = {
            Row {
                Text(event.eventType.title)
                event.description?.also { description ->
                    Spacer(modifier = Modifier.weight(1f))
                    Text(description)
                }
            }
        })
}