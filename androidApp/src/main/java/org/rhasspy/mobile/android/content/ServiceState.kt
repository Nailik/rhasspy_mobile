package org.rhasspy.mobile.android.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.item.EventStateCard
import org.rhasspy.mobile.android.content.item.EventStateIcon
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel


@Composable
fun ServiceStateHeader(viewModel: IConfigurationViewModel) {

    var isShowDialog by remember { mutableStateOf(false) }

    ServiceState(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp))
            .padding(16.dp),
        serviceState = viewModel.serviceState.collectAsState().value,
        enabled = viewModel.isOpenServiceDialogEnabled.collectAsState().value,
        onClick = {
            isShowDialog = true
        }
    )

    if (isShowDialog) {
        AlertDialog(
            onDismissRequest = {
                isShowDialog = false
            },
            title = {
                Text(MR.strings.error)
            },
            text = {
                when (val informationText = viewModel.serviceStateDialogText.collectAsState().value) {
                    is StringResource -> Text(informationText)
                    is String -> androidx.compose.material3.Text(informationText)
                    else -> {}
                }
            },
            icon = {
                Icon(imageVector = Icons.Filled.Info, contentDescription = MR.strings.info)
            },
            confirmButton = {},
            dismissButton = {
                isShowDialog = false
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
    }

}

/**
 * service state information
 */
@Composable
fun ServiceState(
    modifier: Modifier = Modifier,
    serviceState: ServiceState,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
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