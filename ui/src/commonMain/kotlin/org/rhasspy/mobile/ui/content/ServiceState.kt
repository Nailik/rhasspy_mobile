package org.rhasspy.mobile.ui.content

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.item.EventStateCard
import org.rhasspy.mobile.ui.content.item.EventStateIcon
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Composable
fun ServiceStateDialog(
    dialogText: Any,
    onDismissRequest: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismissRequest,
        headline = {
            Text(MR.strings.error.stable)
        },
        supportingText = {
            when (dialogText) {
                is StableStringResource -> Text(dialogText)
                is String -> Text(dialogText)
                else -> Text(dialogText.toString())
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = MR.strings.info.stable
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.close.stable)
            }
        }
    )

}

/**
 * service state information
 */
@Composable
fun ServiceStateHeader(
    modifier: Modifier = Modifier,
    serviceViewState: ServiceViewState,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        val serviceState by serviceViewState.serviceState.collectAsState()
        EventStateCard(
            serviceState = serviceState,
            enabled = enabled,
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
            is Pending -> MR.strings.pending.stable
            is Loading -> MR.strings.loading.stable
            is Success -> MR.strings.success.stable
            is Error -> MR.strings.error.stable
            is Exception -> MR.strings.error.stable
            is Disabled -> MR.strings.disabled.stable
        }
    )

}