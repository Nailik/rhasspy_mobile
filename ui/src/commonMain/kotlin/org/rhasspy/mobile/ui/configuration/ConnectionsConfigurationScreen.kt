package org.rhasspy.mobile.ui.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.toText
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewState.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.ConnectionsConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConnectionScreenNavigationDestination.*

/**
 * configuration screen for connection (rhasspy2Hermes, rhasspy3Wyoming, HomeAssistant, Mqtt, local Webserver)
 */
@Composable
fun ConnectionsConfigurationScreen(viewModel: ConnectionsConfigurationViewModel) {

    ScreenContent(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        ConnectionsConfigurationScreenContent(
            onEvent = viewModel::onEvent,
            viewState = viewState
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectionsConfigurationScreenContent(
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit,
    viewState: ConnectionsConfigurationViewState
) {

    Surface(tonalElevation = 3.dp) {
        Scaffold(
            modifier = Modifier
                .testTag(ConnectionsConfigurationScreen)
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(MR.strings.connections.stable) },
                    navigationIcon = {
                        IconButton(
                            onClick = { onEvent(BackClick) },
                            modifier = Modifier.testTag(TestTag.AppBarBackButton)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = MR.strings.back.stable,
                            )
                        }
                    }
                )
            },
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .testTag(TestTag.List)
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                Rhassyp2Hermes(
                    viewState = viewState.rhassyp2Hermes,
                    onEvent = onEvent
                )

                CustomDivider()

                Rhassyp3Wyoming(
                    viewState = viewState.rhassyp3Wyoming,
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
    }

}

@Composable
private fun Rhassyp2Hermes(
    viewState: HttpViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(Rhasspy2HermesConnectionScreen)) }
            .testTag(Rhasspy2HermesConnectionScreen),
        text = { Text(resource = MR.strings.rhasspy2_hermes_server.stable) },
        secondaryText = { Text(text = viewState.host) },
    )

}

@Composable
private fun Rhassyp3Wyoming(
    viewState: HttpViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(Rhasspy3WyomingConnectionScreen)) }
            .testTag(Rhasspy3WyomingConnectionScreen),
        text = { Text(resource = MR.strings.rhasspy3_wyoming_server.stable) },
        secondaryText = { Text(text = viewState.host) },
    )

}

@Composable
private fun HomeAssistant(
    viewState: HttpViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(HomeAssistantConnectionScreen)) }
            .testTag(HomeAssistantConnectionScreen),
        text = { Text(resource = MR.strings.home_assistant_server.stable) },
        secondaryText = { Text(text = viewState.host) },
    )

}

@Composable
private fun Mqtt(
    viewState: MqttViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(MqttConnectionScreen)) }
            .testTag(MqttConnectionScreen),
        text = { Text(resource = MR.strings.mqtt.stable) },
        secondaryText = {
            Text(resource = if (viewState.isMQTTConnected) MR.strings.connected.stable else MR.strings.notConnected.stable)
        },
    )

}

@Composable
private fun Webserver(
    viewState: WebServerViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(WebServerConnectionScreen)) }
            .testTag(WebServerConnectionScreen),
        text = { Text(resource = MR.strings.local_webserver.stable) },
        secondaryText = { Text(resource = viewState.isHttpServerEnabled.toText()) },
    )

}