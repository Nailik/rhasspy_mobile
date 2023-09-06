package org.rhasspy.mobile.ui.configuration.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationViewState.HttpConfigurationData

/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Composable
fun Rhasspy2HermesConnectionScreen(viewModel: Rhasspy2HermesConnectionConfigurationViewModel) {

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenViewModel = viewModel,
        title = MR.strings.rhasspy2_hermes_server.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        HttpConnectionDetailContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun HttpConnectionDetailContent(
    editData: HttpConfigurationData,
    onEvent: (Rhasspy2HermesConnectionConfigurationUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        //base http endpoint
        TextFieldListItem(
            label = MR.strings.baseHost.stable,
            modifier = Modifier.testTag(TestTag.Host),
            value = editData.host,
            onValueChange = { onEvent(UpdateHomeAssistantClientServerEndpointHost(it)) },
            isLastItem = false
        )

        //timeout
        TextFieldListItem(
            label = MR.strings.requestTimeout.stable,
            modifier = Modifier.testTag(TestTag.Timeout),
            value = editData.timeoutText,
            onValueChange = { onEvent(UpdateHomeAssistantClientTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        //home assistant access token
        TextFieldListItemVisibility(
            modifier = Modifier.testTag(TestTag.AccessToken),
            value = "",
            onValueChange = { },
            label = MR.strings.accessToken.stable,
            action = {
                IconButton(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape),
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = MR.strings.scan_qr_code.stable,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        )

        //switch to toggle validation of SSL certificate
        SwitchListItem(
            text = MR.strings.disableSSLValidation.stable,
            modifier = Modifier.testTag(TestTag.SSLSwitch),
            secondaryText = MR.strings.disableSSLValidationInformation.stable,
            isChecked = editData.isSSLVerificationDisabled,
            onCheckedChange = { onEvent(SetHomeAssistantSSLVerificationDisabled(it)) },
        )

    }

}