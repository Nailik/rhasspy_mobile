package org.rhasspy.mobile.android.content.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.item.EventStateContent
import org.rhasspy.mobile.android.content.item.EventStateIcon
import org.rhasspy.mobile.android.content.item.EventStateIconTinted
import org.rhasspy.mobile.middleware.Event
import org.rhasspy.mobile.middleware.EventState
import org.rhasspy.mobile.middleware.name

@Composable
fun EventListItem(event: Event) {

    val state by event.eventState.collectAsState()

    EventStateContent(
        eventState = state
    ) {
        ListItem(
            leadingContent = { EventStateIconTinted(state) },
            overlineText = {
                Text(event.eventType.name)
            },
            supportingText = {
                val text = when (state) {
                    is EventState.Pending -> translate(MR.strings.pending)
                    is EventState.Loading -> translate(MR.strings.loading)
                    is EventState.Success -> state.information
                    is EventState.Warning -> state.information
                    is EventState.Disabled -> translate(MR.strings.disabled)
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
}