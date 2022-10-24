package org.rhasspy.mobile.android.configuration.content

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.OutlineButtonListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.android.utils.TextFieldListItemVisibility
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.viewModels.configuration.MqttConfigurationViewModel


/**
 * mqtt configuration content
 * Enable/Disable switch
 * when enabled then
 * connection settings (host, port)
 * option to test connection
 * ssl settings
 * connection timeout settings
 */
@Preview
@Composable
fun MqttConfigurationContent(viewModel: MqttConfigurationViewModel = viewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.MqttConfiguration),
        title = MR.strings.mqtt,
        hasUnsavedChanges = MutableStateFlow(false),
        onSave = viewModel::save,
        onTest = viewModel::test,
        onDiscard = {  }
    ) {

        //toggle to turn mqtt enabled on or off
        SwitchListItem(
            text = MR.strings.externalMQTT,
            isChecked = viewModel.isMqttEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleMqttEnabled
        )

        //visibility of mqtt settings
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.isMqttSettingsVisible.collectAsState().value
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
private fun MqttConnectionSettings(viewModel: MqttConfigurationViewModel) {

    //host
    TextFieldListItem(
        label = MR.strings.host,
        value = viewModel.mqttHost.collectAsState().value,
        onValueChange = viewModel::updateMqttHost,
    )

    //port
    TextFieldListItem(
        label = MR.strings.port,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.mqttPort.collectAsState().value,
        onValueChange = viewModel::updateMqttPort,
    )

    //username
    TextFieldListItem(
        value = viewModel.mqttUserName.collectAsState().value,
        onValueChange = viewModel::updateMqttUserName,
        label = MR.strings.userName
    )

    //password
    TextFieldListItemVisibility(
        value = viewModel.mqttPassword.collectAsState().value,
        onValueChange = viewModel::updateMqttPassword,
        label = MR.strings.password
    )
}

/**
 * Button to test mqtt connection
 * is disabled while testing
 * shows testing result
 */
@Composable
private fun MqttTestConnection(viewModel: MqttConfigurationViewModel) {
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
        enabled = viewModel.isMqttTestEnabled.collectAsState().value,
        onClick = viewModel::testMqttConnection
    )
}

/**
 * switch to enable mqtt ssl
 * button to select certificate
 */
@Composable
private fun MqttSSL(viewModel: MqttConfigurationViewModel) {

    SwitchListItem(
        text = MR.strings.enableSSL,
        isChecked = viewModel.isMqttSSLEnabled.collectAsState().value,
        onCheckedChange = viewModel::toggleMqttSSLEnabled
    )

    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.isMqttChooseCertificateVisible.collectAsState().value
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
private fun MqttConnectionTiming(viewModel: MqttConfigurationViewModel) {

    TextFieldListItem(
        label = MR.strings.connectionTimeout,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.mqttConnectionTimeout.collectAsState().value,
        onValueChange = viewModel::updateMqttConnectionTimeout
    )

    TextFieldListItem(
        label = MR.strings.keepAliveInterval,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.mqttKeepAliveInterval.collectAsState().value,
        onValueChange = viewModel::updateMqttKeepAliveInterval
    )

    TextFieldListItem(
        label = MR.strings.retryInterval,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        value = viewModel.mqttRetryInterval.collectAsState().value,
        onValueChange = viewModel::updateMqttRetryInterval
    )

}