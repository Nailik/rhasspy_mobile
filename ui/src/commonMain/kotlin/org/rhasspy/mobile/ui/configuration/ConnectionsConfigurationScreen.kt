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
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.*
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewState.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.ConnectionsConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConnectionScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConnectionScreenNavigationDestination.*

/**
 * configuration screens with list items that open bottom sheet
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

                Http(
                    viewState = viewState.http,
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

/**
 * List element for http configuration
 * shows if ssl verification is enabled
 */
@Composable
private fun Http(
    viewState: HttpViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ConnectionListItem(
        text = MR.strings.remote_http.stable,
        secondaryText = "${viewState.httpConnectionCount} ${translate(MR.strings.server.stable)}",
        destination = HttpConnectionListScreen,
        onEvent = onEvent
    )

}

/**
 * List element for mqtt configuration
 * shows connection state of mqtt
 */
@Composable
private fun Mqtt(
    viewState: MqttViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ConnectionListItem(
        text = MR.strings.mqtt.stable,
        secondaryText = if (viewState.isMQTTConnected) MR.strings.connected.stable else MR.strings.notConnected.stable,
        destination = MqttConnectionScreen,
        onEvent = onEvent
    )

}


/**
 * List element for text to speech configuration
 * shows if web server is enabled
 */
@Composable
private fun Webserver(
    viewState: WebServerViewState,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {

    ConnectionListItem(
        text = MR.strings.webserver.stable,
        secondaryText = viewState.isHttpServerEnabled.toText(),
        destination = WebServerConnectionScreen,
        onEvent = onEvent
    )

}

/**
 * list item
 */
@Composable
private fun ConnectionListItem(
    text: StableStringResource,
    secondaryText: String,
    destination: ConnectionScreenNavigationDestination,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {
    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(destination)) }
            .testTag(destination),
        text = { Text(text) },
        secondaryText = { Text(text = secondaryText) },
    )
}

/**
 * list item
 */
@Composable
private fun ConnectionListItem(
    text: StableStringResource,
    secondaryText: StableStringResource,
    destination: ConnectionScreenNavigationDestination,
    onEvent: (ConnectionsConfigurationUiEvent) -> Unit
) {
    ListElement(
        modifier = Modifier
            .clickable { onEvent(Navigate(destination)) }
            .testTag(destination),
        text = { Text(text) },
        secondaryText = { Text(resource = secondaryText) },
    )
}