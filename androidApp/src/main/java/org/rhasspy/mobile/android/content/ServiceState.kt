package org.rhasspy.mobile.android.content

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.item.EventStateCard
import org.rhasspy.mobile.android.content.item.EventStateIcon
import org.rhasspy.mobile.middleware.ServiceState

/**
 * service state information
 */
@Composable
fun ServiceState(
    modifier: Modifier = Modifier,
    serviceState: ServiceState,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        EventStateCard(
            serviceState = serviceState,
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
private fun ServiceStateText(serviceState: ServiceState) {

    Text(
        resource = when (serviceState) {
            is ServiceState.Pending -> MR.strings.pending
            is ServiceState.Loading -> MR.strings.loading
            is ServiceState.Success -> MR.strings.success
            is ServiceState.Warning -> MR.strings.warning
            is ServiceState.Error -> MR.strings.error
            is ServiceState.Exception -> MR.strings.error
            is ServiceState.Disabled -> MR.strings.disabled
        }
    )

}