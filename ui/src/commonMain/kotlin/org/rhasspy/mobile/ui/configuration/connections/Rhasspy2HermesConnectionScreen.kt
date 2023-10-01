package org.rhasspy.mobile.ui.configuration.connections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ConnectionStateHeaderItem
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationUiEvent.Action.AccessTokenQRCodeClick
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationViewState.Rhasspy2HermesConnectionConfigurationData

/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Composable
fun Rhasspy2HermesConnectionScreen(viewModel: Rhasspy2HermesConnectionConfigurationViewModel) {

    ScreenContent(
        title = MR.strings.rhasspy2_hermes_server.stable,
        viewModel = viewModel,
        tonalElevation = 1.dp,
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val viewState by viewModel.viewState.collectAsState()

            ConnectionStateHeaderItem(
                connectionStateFlow = viewState.connectionState,
            )

            HttpConnectionDetailContent(
                editData = viewState.editData,
                onEvent = viewModel::onEvent
            )

        }
    }

}

@Composable
private fun ColumnScope.HttpConnectionDetailContent(
    editData: Rhasspy2HermesConnectionConfigurationData,
    onEvent: (Rhasspy2HermesConnectionConfigurationUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
    ) {

        //base http endpoint
        TextFieldListItem(
            label = MR.strings.baseHost.stable,
            modifier = Modifier.testTag(TestTag.Host),
            value = editData.host,
            onValueChange = { onEvent(UpdateRhasspy2HermesServerEndpointHost(it)) },
            isLastItem = false,
        )

        //timeout
        TextFieldListItem(
            label = MR.strings.requestTimeout.stable,
            modifier = Modifier.testTag(TestTag.Timeout),
            value = editData.timeoutText,
            onValueChange = { onEvent(UpdateRhasspy2HermesTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isLastItem = false,
        )

        //home assistant access token
        TextFieldListItemVisibility(
            modifier = Modifier.testTag(TestTag.AccessToken),
            value = editData.bearerToken,
            onValueChange = { onEvent(UpdateRhasspy2HermesAccessToken(it)) },
            label = MR.strings.accessToken.stable,
            action = {
                IconButton(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape),
                    onClick = { onEvent(AccessTokenQRCodeClick) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = MR.strings.scan_qr_code.stable,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            },
            isLastItem = true,
        )

        //switch to toggle validation of SSL certificate
        SwitchListItem(
            text = MR.strings.disableSSLValidation.stable,
            modifier = Modifier.testTag(TestTag.SSLSwitch),
            secondaryText = MR.strings.disableSSLValidationInformation.stable,
            isChecked = editData.isSSLVerificationDisabled,
            onCheckedChange = { onEvent(SetRhasspy2HermesSSLVerificationDisabled(it)) },
        )

    }

}