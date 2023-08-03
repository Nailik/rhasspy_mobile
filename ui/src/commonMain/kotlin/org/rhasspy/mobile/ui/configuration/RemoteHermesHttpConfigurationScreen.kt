package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationViewState.RemoteHermesHttpConfigurationData

/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Composable
fun RemoteHermesHttpConfigurationScreen() {

    val viewModel: RemoteHermesHttpConfigurationViewModel =
        LocalViewModelFactory.current.getViewModel()

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenViewModel = viewModel,
        title = MR.strings.remoteHermesHTTP.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        WebServerEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun WebServerEditContent(
    editData: RemoteHermesHttpConfigurationData,
    onEvent: (RemoteHermesHttpConfigurationUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            //base http endpoint
            TextFieldListItem(
                label = MR.strings.baseHost.stable,
                modifier = Modifier.testTag(TestTag.Host),
                value = editData.httpClientServerEndpointHost,
                onValueChange = { onEvent(UpdateHttpClientServerEndpointHost(it)) },
                isLastItem = false
            )
        }

        item {
            //port
            TextFieldListItem(
                label = MR.strings.port.stable,
                modifier = Modifier.testTag(TestTag.Port),
                value = editData.httpClientServerEndpointPortText,
                onValueChange = { onEvent(UpdateHttpClientServerEndpointPort(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }

        item {
            //timeout
            TextFieldListItem(
                label = MR.strings.requestTimeout.stable,
                modifier = Modifier.testTag(TestTag.Timeout),
                value = editData.httpClientTimeoutText,
                onValueChange = { onEvent(UpdateHttpClientTimeout(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }

        item {
            //switch to toggle validation of SSL certificate
            SwitchListItem(
                text = MR.strings.disableSSLValidation.stable,
                modifier = Modifier.testTag(TestTag.SSLSwitch),
                secondaryText = MR.strings.disableSSLValidationInformation.stable,
                isChecked = editData.isHttpSSLVerificationDisabled,
                onCheckedChange = { onEvent(SetHttpSSLVerificationDisabled(it)) },
            )
        }

    }

}