package org.rhasspy.mobile.viewmodel.configuration.connection.mqtt

import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewState.MqttConnectionConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MqttConfigurationViewModelTest : AppTest() {

    private lateinit var mqttConfigurationViewModel: MqttConnectionConfigurationViewModel

    private lateinit var initialMqttConnectionData: MqttConnectionConfigurationData
    private lateinit var mqttConnectionData: MqttConnectionConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialMqttConnectionData = MqttConnectionConfigurationData(
            isEnabled = false,
            host = "tcp://<server>:1883",
            userName = "",
            password = "",
            isSSLEnabled = false,
            connectionTimeout = 5,
            keepAliveInterval = 30,
            retryInterval = 10L,
            keystoreFile = null
        )

        mqttConnectionData = MqttConnectionConfigurationData(
            isEnabled = true,
            host = getRandomString(5),
            userName = getRandomString(5),
            password = getRandomString(5),
            isSSLEnabled = true,
            connectionTimeout = 6914,
            keepAliveInterval = 9154,
            retryInterval = 1958L,
            keystoreFile = getRandomString(5)
        )

        mqttConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(
            initialMqttConnectionData,
            mqttConfigurationViewModel.viewState.value.editData
        )

        with(mqttConnectionData) {
            mqttConfigurationViewModel.onEvent(SetMqttEnabled(isEnabled))
            mqttConfigurationViewModel.onEvent(SetMqttSSLEnabled(isSSLEnabled))
            mqttConfigurationViewModel.onEvent(UpdateMqttConnectionTimeout(connectionTimeout.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttHost(host))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeepAliveInterval(keepAliveInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttPassword(password))
            mqttConfigurationViewModel.onEvent(UpdateMqttRetryInterval(retryInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttUserName(userName))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeyStoreFile(keystoreFile?.toPath()!!))
        }

        assertEquals(mqttConnectionData, mqttConfigurationViewModel.viewState.value.editData)

        mqttConfigurationViewModel.onEvent(Save)

        assertEquals(mqttConnectionData, mqttConfigurationViewModel.viewState.value.editData)
        assertEquals(mqttConnectionData, MqttConnectionConfigurationDataMapper().invoke(ConfigurationSetting.mqttConnection.value))
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(
            initialMqttConnectionData,
            mqttConfigurationViewModel.viewState.value.editData
        )

        with(mqttConnectionData) {
            mqttConfigurationViewModel.onEvent(SetMqttEnabled(isEnabled))
            mqttConfigurationViewModel.onEvent(SetMqttSSLEnabled(isSSLEnabled))
            mqttConfigurationViewModel.onEvent(UpdateMqttConnectionTimeout(connectionTimeout.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttHost(host))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeepAliveInterval(keepAliveInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttPassword(password))
            mqttConfigurationViewModel.onEvent(UpdateMqttRetryInterval(retryInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttUserName(userName))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeyStoreFile(keystoreFile?.toPath()!!))
        }

        assertEquals(mqttConnectionData, mqttConfigurationViewModel.viewState.value.editData)

        mqttConfigurationViewModel.onEvent(Discard)

        assertEquals(
            initialMqttConnectionData,
            mqttConfigurationViewModel.viewState.value.editData
        )
        assertEquals(initialMqttConnectionData, MqttConnectionConfigurationDataMapper().invoke(ConfigurationSetting.mqttConnection.value))
    }
}