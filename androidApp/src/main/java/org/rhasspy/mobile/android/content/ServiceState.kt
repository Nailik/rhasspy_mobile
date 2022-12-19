package org.rhasspy.mobile.android.content

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.item.EventStateCard
import org.rhasspy.mobile.android.content.item.EventStateIcon
import org.rhasspy.mobile.middleware.EventState

/**
 * service state information
 */
@Composable
fun ServiceState(
    modifier: Modifier = Modifier,
    serviceState: EventState,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        EventStateCard(
            eventState = serviceState,
            onClick = onClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EventStateIcon(serviceState)
                ServiceStateText(serviceState)
            }
        }
    }
}

/**
 * text of service state
 */
@Composable
private fun ServiceStateText(serviceState: EventState) {

    Text(
        resource = when (serviceState) {
            is EventState.Pending -> MR.strings.pending
            is EventState.Loading -> MR.strings.loading
            is EventState.Success -> MR.strings.success
            is EventState.Warning -> MR.strings.warning
            is EventState.Error -> MR.strings.error
            is EventState.Disabled -> MR.strings.disabled
        }
    )

}