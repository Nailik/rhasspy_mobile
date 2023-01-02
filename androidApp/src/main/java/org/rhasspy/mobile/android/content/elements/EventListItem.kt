package org.rhasspy.mobile.android.content.elements

/*
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
import org.rhasspy.mobile.android.content.item.EventStateIconTinted
import org.rhasspy.mobile.middleware.ServiceState

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
                    is ServiceState.Pending -> translate(MR.strings.pending)
                    is ServiceState.Loading -> translate(MR.strings.loading)
                    is ServiceState.Success -> state.information
                    is ServiceState.Warning -> state.information
                    is ServiceState.Disabled -> translate(MR.strings.disabled)
                    is ServiceState.Error -> state.toString()
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
}*/