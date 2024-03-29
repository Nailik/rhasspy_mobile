package org.rhasspy.mobile.ui.configuration.connection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.*
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Action.OpenMqttSSLWiki
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Action.SelectSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewState.MqttConnectionConfigurationData

/**
 * mqtt configuration content
 * Enable/Disable switch
 * when enabled then
 * connection settings (host, port)
 * option to test connection
 * ssl settings
 * connection timeout settings
 */
@Composable
fun MqttConnectionScreen(viewModel: MqttConnectionConfigurationViewModel) {

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenViewModel = viewModel,
        title = MR.strings.mqtt.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        MqttConnectionEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun MqttConnectionEditContent(
    editData: MqttConnectionConfigurationData,
    onEvent: (MqttConnectionConfigurationUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            //toggle to turn mqtt enabled on or off
            SwitchListItem(
                text = MR.strings.externalMQTT.stable,
                modifier = Modifier.testTag(TestTag.MqttSwitch),
                isChecked = editData.isEnabled,
                onCheckedChange = { onEvent(SetMqttEnabled(it)) }
            )
        }

        item {
            //visibility of mqtt settings
            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = editData.isEnabled
            ) {

                Column {

                    MqttConnectionSettings(
                        mqttHost = editData.host,
                        mqttUserName = editData.userName,
                        mqttPassword = editData.password,
                        onEvent = onEvent
                    )

                    MqttSSL(
                        isMqttSSLEnabled = editData.isSSLEnabled,
                        mqttKeyStoreFileName = editData.keystoreFile,
                        onEvent = onEvent
                    )

                    MqttConnectionTiming(
                        mqttConnectionTimeoutText = editData.connectionTimeoutText,
                        mqttKeepAliveIntervalText = editData.keepAliveIntervalText,
                        mqttRetryIntervalText = editData.retryIntervalText,
                        onEvent = onEvent
                    )

                }

            }
        }

    }

}


/**
 * connection settings for mqtt
 * text fields for
 * host, port, username, password
 */
@Composable
private fun MqttConnectionSettings(
    mqttHost: String,
    mqttUserName: String,
    mqttPassword: String,
    onEvent: (MqttConnectionConfigurationUiEvent) -> Unit
) {

    //host
    TextFieldListItem(
        label = MR.strings.host.stable,
        modifier = Modifier.testTag(TestTag.Host),
        value = mqttHost,
        onValueChange = { onEvent(UpdateMqttHost(it)) },
        isLastItem = false
    )

    //username
    TextFieldListItem(
        label = MR.strings.userName.stable,
        modifier = Modifier.testTag(TestTag.UserName),
        value = mqttUserName,
        onValueChange = { onEvent(UpdateMqttUserName(it)) },
        isLastItem = false
    )

    //password
    TextFieldListItemVisibility(
        label = MR.strings.password.stable,
        modifier = Modifier.testTag(TestTag.Password),
        value = mqttPassword,
        onValueChange = { onEvent(UpdateMqttPassword(it)) }
    )
}

/**
 * switch to enable mqtt ssl
 * button to select certificate
 */
@Composable
private fun MqttSSL(
    isMqttSSLEnabled: Boolean,
    mqttKeyStoreFileName: String?,
    onEvent: (MqttConnectionConfigurationUiEvent) -> Unit
) {

    SwitchListItem(
        text = MR.strings.enableSSL.stable,
        modifier = Modifier.testTag(TestTag.SSLSwitch),
        isChecked = isMqttSSLEnabled,
        onCheckedChange = { onEvent(SetMqttSSLEnabled(it)) }
    )

    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = isMqttSSLEnabled
    ) {

        Column {

            ListElement(
                modifier = Modifier
                    .testTag(TestTag.MQTTSSLWiki)
                    .clickable(onClick = { onEvent(OpenMqttSSLWiki) }),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = MR.strings.sslWiki.stable
                    )
                },
                text = { Text(MR.strings.sslWiki.stable) },
                secondaryText = { Text(MR.strings.sslWikiInfo.stable) }
            )

            FilledTonalButtonListItem(
                text = MR.strings.chooseCertificate.stable,
                modifier = Modifier.testTag(TestTag.CertificateButton),
                onClick = { onEvent(SelectSSLCertificate) }
            )

            val isKeyStoreFileTextVisible by remember { derivedStateOf { mqttKeyStoreFileName != null } }

            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = isKeyStoreFileTextVisible
            ) {

                val keyStoreFileText by remember { derivedStateOf { mqttKeyStoreFileName ?: "" } }

                InformationListElement(
                    text = translate(
                        resource = MR.strings.currentlySelectedCertificate.stable,
                        keyStoreFileText
                    )
                )
            }

        }
    }

}

/**
 * Time settings for mqtt connection
 * timeout, keepAliveInterval, retryInterval
 */
@Composable
private fun MqttConnectionTiming(
    mqttConnectionTimeoutText: String,
    mqttKeepAliveIntervalText: String,
    mqttRetryIntervalText: String,
    onEvent: (MqttConnectionConfigurationUiEvent) -> Unit
) {

    TextFieldListItem(
        label = MR.strings.connectionTimeout.stable,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.testTag(TestTag.ConnectionTimeout),
        value = mqttConnectionTimeoutText,
        onValueChange = { onEvent(UpdateMqttConnectionTimeout(it)) },
        isLastItem = false
    )

    TextFieldListItem(
        label = MR.strings.keepAliveInterval.stable,
        modifier = Modifier.testTag(TestTag.KeepAliveInterval),
        value = mqttKeepAliveIntervalText,
        onValueChange = { onEvent(UpdateMqttKeepAliveInterval(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        isLastItem = false
    )

    TextFieldListItem(
        label = MR.strings.retryInterval.stable,
        modifier = Modifier.testTag(TestTag.RetryInterval),
        value = mqttRetryIntervalText,
        onValueChange = { onEvent(UpdateMqttRetryInterval(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )

}