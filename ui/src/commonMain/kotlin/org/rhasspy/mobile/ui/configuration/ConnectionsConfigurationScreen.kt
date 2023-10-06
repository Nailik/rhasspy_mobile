package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.toText
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.item.EventStateIconTinted
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.TonalElevationLevel1
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewState.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConnectionScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConnectionScreenNavigationDestination.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

/**
 * configuration screen for connection (rhasspy2Hermes, rhasspy3Wyoming, HomeAssistant, Mqtt, local Webserver)
 */
@Composable
fun ConnectionsConfigurationScreen(viewModel: ConnectionsConfigurationViewModel) {

    ScreenContent(
        title = MR.strings.connections.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel1,
    ) {

        val viewState by viewModel.viewState.collectAsState()

        ConnectionsConfigurationScreenContent(
            onEvent = viewModel::onEvent,
            viewState = viewState
        )
    }

}

@Composable
private fun ConnectionsConfigurationScreenContent(
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit,
    viewState: ConnectionsConfigurationViewState
) {

    Column(
        modifier = Modifier
            .testTag(TestTag.List)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Rhasspy2Hermes(
            viewState = viewState.rhasspy2Hermes,
            onEvent = onEvent
        )

        CustomDivider()

        Rhasspy3Wyoming(
            viewState = viewState.rhasspy3Wyoming,
            onEvent = onEvent
        )

        CustomDivider()

        HomeAssistant(
            viewState = viewState.homeAssistant,
            onEvent = onEvent
        )

        CustomDivider()

        Mqtt(
            viewState = viewState.mqtt,
            onEvent = onEvent
        )

        CustomDivider()


        Webserver(
            viewState = viewState.webserver,
            onEvent = onEvent
        )

        CustomDivider()

    }

}

@Composable
private fun Rhasspy2Hermes(
    viewState: HttpViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ConnectionListItem(
        text = MR.strings.rhasspy2_hermes_server.stable,
        secondaryText = viewState.host,
        serviceViewState = viewState.serviceViewState,
        destination = Rhasspy2HermesConnectionScreen,
        onEvent = onEvent
    )

}

@Composable
private fun Rhasspy3Wyoming(
    viewState: HttpViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ConnectionListItem(
        text = MR.strings.rhasspy3_wyoming_server.stable,
        secondaryText = viewState.host,
        serviceViewState = viewState.serviceViewState,
        destination = Rhasspy3WyomingConnectionScreen,
        onEvent = onEvent
    )

}

@Composable
private fun HomeAssistant(
    viewState: HttpViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ConnectionListItem(
        text = MR.strings.home_assistant_server.stable,
        secondaryText = viewState.host,
        serviceViewState = viewState.serviceViewState,
        destination = HomeAssistantConnectionScreen,
        onEvent = onEvent
    )

}

@Composable
private fun Mqtt(
    viewState: MqttViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ConnectionListItem(
        text = MR.strings.mqtt.stable,
        secondaryText = viewState.isMQTTEnabled.toText(),
        serviceViewState = viewState.serviceViewState,
        destination = MqttConnectionScreen,
        onEvent = onEvent
    )

}

@Composable
private fun Webserver(
    viewState: WebServerViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ConnectionListItem(
        text = MR.strings.local_webserver.stable,
        secondaryText = viewState.isHttpServerEnabled.toText(),
        serviceViewState = viewState.serviceViewState,
        destination = WebServerConnectionScreen,
        onEvent = onEvent
    )

}

@Composable
private fun ConnectionListItem(
    text: StableStringResource,
    secondaryText: StableStringResource,
    serviceViewState: ServiceViewState? = null,
    destination: ConnectionScreenNavigationDestination,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {
    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(destination)) }
            .testTag(destination),
        text = { Text(text) },
        secondaryText = { Text(secondaryText) },
        trailing = serviceViewState?.let {
            {
                val serviceStateValue by serviceViewState.connectionState.collectAsState()
                EventStateIconTinted(serviceStateValue)
            }
        }
    )

}

@Composable
private fun ConnectionListItem(
    text: StableStringResource,
    secondaryText: String,
    serviceViewState: ServiceViewState? = null,
    destination: ConnectionScreenNavigationDestination,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {
    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(destination)) }
            .testTag(destination),
        text = { Text(text) },
        secondaryText = { Text("${translate(MR.strings.host.stable)}: $secondaryText") },
        trailing = serviceViewState?.let {
            {
                val serviceStateValue by serviceViewState.connectionState.collectAsState()
                EventStateIconTinted(serviceStateValue)
            }
        }
    )

}