package org.rhasspy.mobile.ui.configuration.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.connection.HttpConnection
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.LocalViewModelFactory
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.CheckBoxListItem
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.content.list.TitleListElement
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationViewState.RemoteHermesHttpConfigurationData

/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Composable
fun HttpConnectionDetailScreen(id: HttpConnection?) {

    val viewModel: HttpConnectionDetailConfigurationViewModel = LocalViewModelFactory.current.getViewModel(id)

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenViewModel = viewModel,
        title = MR.strings.remote_http.stable,
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
    editData: RemoteHermesHttpConfigurationData,
    onEvent: (HttpConnectionDetailConfigurationUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {

            TitleListElement {
                Text(resource = MR.strings.protocols.stable)
            }

            CheckBoxListItem(
                modifier = Modifier.testTag(TestTag.AudioFocusOnSound),
                text = MR.strings.rhasspy2_hermes.stable,
                secondaryText = MR.strings.rhasspy2_hermes_info.stable,
                isChecked = editData.isHermes,
                onCheckedChange = { }
            )

            CheckBoxListItem(
                modifier = Modifier.testTag(TestTag.AudioFocusOnRecord),
                text = MR.strings.rhasspy3_wyoming.stable,
                secondaryText = MR.strings.rhasspy3_wyoming_info.stable,
                isChecked = editData.isWyoming,
                onCheckedChange = { }
            )

            CheckBoxListItem(
                modifier = Modifier.testTag(TestTag.AudioFocusOnDialog),
                text = MR.strings.home_assistant.stable,
                secondaryText = MR.strings.home_assistant_info.stable,
                isChecked = editData.isHomeAssistant,
                onCheckedChange = { }
            )


        }

        //base http endpoint
        TextFieldListItem(
            label = MR.strings.baseHost.stable,
            modifier = Modifier.testTag(TestTag.Host),
            value = editData.httpClientServerEndpointHost,
            onValueChange = { onEvent(UpdateHttpClientServerEndpointHost(it)) },
            isLastItem = false
        )

        //port
        TextFieldListItem(
            label = MR.strings.port.stable,
            modifier = Modifier.testTag(TestTag.Port),
            value = editData.httpClientServerEndpointPortText,
            onValueChange = { onEvent(UpdateHttpClientServerEndpointPort(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isLastItem = false
        )

        //timeout
        TextFieldListItem(
            label = MR.strings.requestTimeout.stable,
            modifier = Modifier.testTag(TestTag.Timeout),
            value = editData.httpClientTimeoutText,
            onValueChange = { onEvent(UpdateHttpClientTimeout(it)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        //switch to toggle validation of SSL certificate
        SwitchListItem(
            text = MR.strings.disableSSLValidation.stable,
            modifier = Modifier.testTag(TestTag.SSLSwitch),
            secondaryText = MR.strings.disableSSLValidationInformation.stable,
            isChecked = editData.isSSLVerificationDisabled,
            onCheckedChange = { onEvent(SetHttpSSLVerificationDisabled(it)) },
        )

    }

}