package org.rhasspy.mobile.ui.configuration

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.ui.content.list.InformationListElement
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItem
import org.rhasspy.mobile.ui.content.list.TextFieldListItemVisibility
import org.rhasspy.mobile.ui.main.ConfigurationScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action.OpenMqttSSLWiki
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action.RemoveSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action.SelectSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.SetMqttEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.SetMqttSSLEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttConnectionTimeout
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttHost
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttKeepAliveInterval
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttKeyStorePassword
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttPassword
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttPort
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttRetryInterval
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttUserName
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationViewState.MqttConfigurationData

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
fun MqttConfigurationScreen() {

    val viewModel: MqttConfigurationViewModel = LocalViewModelFactory.current.getViewModel()

    val configurationEditViewState by viewModel.configurationViewState.collectAsState()

    ConfigurationScreenItemContent(
        modifier = Modifier,
        screenViewModel = viewModel,
        title = MR.strings.mqtt.stable,
        viewState = configurationEditViewState,
        onEvent = viewModel::onEvent
    ) {

        val viewState by viewModel.viewState.collectAsState()

        MqttEditContent(
            editData = viewState.editData,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun MqttEditContent(
    editData: MqttConfigurationData,
    onEvent: (MqttConfigurationUiEvent) -> Unit,
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
                isChecked = editData.isMqttEnabled,
                onCheckedChange = { onEvent(SetMqttEnabled(it)) }
            )
        }

        item {
            //visibility of mqtt settings
            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = editData.isMqttEnabled
            ) {

                Column {

                    MqttConnectionSettings(
                        mqttHost = editData.mqttHost,
                        mqttPortText = editData.mqttPortText,
                        mqttUserName = editData.mqttUserName,
                        mqttPassword = editData.mqttPassword,
                        onEvent = onEvent
                    )

                    MqttSSL(
                        isMqttSSLEnabled = editData.isMqttSSLEnabled,
                        mqttKeyStoreFileName = editData.mqttKeyStoreFileName,
                        isKeyStoreFileTextVisible = editData.isKeyStoreFileTextVisible,
                        mqttKeyStorePassword = editData.mqttKeyStorePassword,
                        onEvent = onEvent
                    )

                    MqttConnectionTiming(
                        mqttConnectionTimeoutText = editData.mqttConnectionTimeoutText,
                        mqttKeepAliveIntervalText = editData.mqttKeepAliveIntervalText,
                        mqttRetryIntervalText = editData.mqttRetryIntervalText,
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
    mqttPortText: String,
    mqttUserName: String,
    mqttPassword: String,
    onEvent: (MqttConfigurationUiEvent) -> Unit,
) {

    //host
    TextFieldListItem(
        label = MR.strings.host.stable,
        modifier = Modifier.testTag(TestTag.Host),
        value = mqttHost,
        onValueChange = { onEvent(UpdateMqttHost(it)) },
        isLastItem = false
    )

    //port
    TextFieldListItem(
        label = MR.strings.port.stable,
        modifier = Modifier.testTag(TestTag.Port),
        value = mqttPortText,
        onValueChange = { onEvent(UpdateMqttPort(it)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
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
    mqttKeyStoreFileName: String,
    isKeyStoreFileTextVisible: Boolean,
    mqttKeyStorePassword: String,
    onEvent: (MqttConfigurationUiEvent) -> Unit,
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

            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = isKeyStoreFileTextVisible
            ) {
                Column {
                    InformationListElement(
                        text = translate(
                            resource = MR.strings.currentlySelectedCertificate.stable,
                            mqttKeyStoreFileName
                        )
                    )

                    FilledTonalButtonListItem(
                        text = MR.strings.remove.stable,
                        modifier = Modifier.testTag(TestTag.Delete),
                        onClick = { onEvent(RemoveSSLCertificate) }
                    )
                }
            }

            // show the current HTTP server keystore password (read-only)
            TextFieldListItemVisibility(
                label = MR.strings.keyStorePassword.stable,
                value = mqttKeyStorePassword,
                onValueChange = { onEvent(UpdateMqttKeyStorePassword(it)) }
            )

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
    onEvent: (MqttConfigurationUiEvent) -> Unit,
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