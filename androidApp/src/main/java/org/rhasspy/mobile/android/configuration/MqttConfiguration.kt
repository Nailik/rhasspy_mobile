package org.rhasspy.mobile.android.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.screens.ConfigurationScreens
import org.rhasspy.mobile.android.utils.ConfigurationListContent
import org.rhasspy.mobile.android.utils.ConfigurationListItem
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.OutlineButtonListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * List element for mqtt configuration
 * shows connection state of mqtt
 */
@Composable
fun MqttConfigurationItem(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListItem(
        text = MR.strings.mqtt,
        secondaryText = if (viewModel.isMQTTConnected.collectAsState().value) MR.strings.connected else MR.strings.notConnected,
        screen = ConfigurationScreens.Mqtt
    )

}

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
fun MqttConfigurationContent(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListContent(MR.strings.mqtt) {

        val isMqttEnabled by viewModel.isMqttEnabled.flow.collectAsState()

        SwitchListItem(
            text = MR.strings.externalMQTT,
            isChecked = isMqttEnabled,
            onCheckedChange = viewModel.isMqttEnabled::set
        )

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = isMqttEnabled
        ) {

            Column {

                MqttConnectionSettings(viewModel)

                MqttTestConnection(viewModel)

                MqttSSL(viewModel)

                MqttConnectionTiming(viewModel)

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
private fun MqttConnectionSettings(viewModel: ConfigurationScreenViewModel) {

    TextFieldListItem(
        label = MR.strings.host,
        value = viewModel.mqttHost.flow.collectAsState().value,
        onValueChange = viewModel.mqttHost::set,
    )

    TextFieldListItem(
        label = MR.strings.port,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.mqttPort.flow.collectAsState().value,
        onValueChange = viewModel.mqttPort::set,
    )

    TextFieldListItem(
        value = viewModel.mqttUserName.flow.collectAsState().value,
        onValueChange = viewModel.mqttUserName::set,
        label = MR.strings.userName
    )

    var isShowPassword by rememberSaveable { mutableStateOf(false) }

    TextFieldListItem(
        value = viewModel.mqttPassword.flow.collectAsState().value,
        onValueChange = viewModel.mqttPassword::set,
        label = MR.strings.password,
        visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { isShowPassword = !isShowPassword }) {
                Icon(
                    if (isShowPassword) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    },
                    contentDescription = MR.strings.visibility,
                )
            }
        },
    )
}

/**
 * Button to test mqtt connection
 * is disabled while testing
 * shows testing result
 */
@Composable
private fun MqttTestConnection(viewModel: ConfigurationScreenViewModel) {
    OutlineButtonListItem(
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (viewModel.isTestingMqttConnection.collectAsState().value) {

                    val infiniteTransition = rememberInfiniteTransition()
                    val angle by infiniteTransition.animateFloat(
                        initialValue = 0F,
                        targetValue = 360F,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing)
                        )
                    )

                    Icon(
                        modifier = Modifier.rotate(angle),
                        imageVector = Icons.Filled.Autorenew,
                        contentDescription = MR.strings.reset
                    )
                }

                viewModel.testingMqttError.collectAsState().value?.also {
                    Text(text = "${translate(MR.strings.error)}: ${it.statusCode.name}")
                } ?: run {
                    Text(MR.strings.testConnection)
                }
            }
        },
        enabled = viewModel.isMQTTTestEnabled.collectAsState().value,
        onClick = viewModel::testMqttConnection
    )
}

/**
 * switch to enable mqtt ssl
 * button to select certificate
 */
@Composable
private fun MqttSSL(viewModel: ConfigurationScreenViewModel) {

    val isMqttSSL by viewModel.isMqttSSLEnabled.flow.collectAsState()

    SwitchListItem(
        text = MR.strings.enableSSL,
        isChecked = isMqttSSL,
        onCheckedChange = viewModel.isMqttSSLEnabled::set
    )

    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = isMqttSSL
    ) {
        OutlineButtonListItem(
            text = MR.strings.chooseCertificate,
            onClick = { })
    }
}

/**
 * Time settings for mqtt connection
 * timeout, keepAliveInterval, retryInterval
 */
@Composable
private fun MqttConnectionTiming(viewModel: ConfigurationScreenViewModel) {
    TextFieldListItem(
        label = MR.strings.connectionTimeout,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.mqttConnectionTimeout.flow.collectAsState().value,
        onValueChange = viewModel.mqttConnectionTimeout::set
    )

    TextFieldListItem(
        label = MR.strings.keepAliveInterval,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.mqttKeepAliveInterval.flow.collectAsState().value,
        onValueChange = viewModel.mqttKeepAliveInterval::set
    )

    TextFieldListItem(
        label = MR.strings.retryInterval,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.mqttRetryInterval.flow.collectAsState().value,
        onValueChange = viewModel.mqttRetryInterval::set
    )
}