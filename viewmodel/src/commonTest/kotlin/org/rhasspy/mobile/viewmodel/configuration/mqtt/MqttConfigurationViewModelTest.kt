package org.rhasspy.mobile.viewmodel.configuration.mqtt

import kotlinx.coroutines.test.runTest
import okio.Path
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewState.MqttConnectionData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MqttConfigurationViewModelTest : AppTest() {

    private lateinit var mqttConfigurationViewModel: MqttConnectionConfigurationViewModel

    private lateinit var initialMqttConnectionData: MqttConnectionData
    private lateinit var mqttConnectionData: MqttConnectionData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialMqttConnectionData = MqttConnectionData(
            isMqttEnabled = false,
            mqttHost = "",
            mqttPort = 1883,
            mqttUserName = "",
            mqttPassword = "",
            isMqttSSLEnabled = false,
            mqttConnectionTimeout = 5L,
            mqttKeepAliveInterval = 30L,
            mqttRetryInterval = 10L,
            mqttKeyStoreFile = null
        )

        mqttConnectionData = MqttConnectionData(
            isMqttEnabled = true,
            mqttHost = getRandomString(5),
            mqttPort = 1652,
            mqttUserName = getRandomString(5),
            mqttPassword = getRandomString(5),
            isMqttSSLEnabled = true,
            mqttConnectionTimeout = 6914L,
            mqttKeepAliveInterval = 9154L,
            mqttRetryInterval = 1958L,
            mqttKeyStoreFile = Path.commonInternalPath(get(), getRandomString(5))
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
            mqttConfigurationViewModel.onEvent(SetMqttEnabled(isMqttEnabled))
            mqttConfigurationViewModel.onEvent(SetMqttSSLEnabled(isMqttSSLEnabled))
            mqttConfigurationViewModel.onEvent(UpdateMqttConnectionTimeout(mqttConnectionTimeout.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttHost(mqttHost))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeepAliveInterval(mqttKeepAliveInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttPassword(mqttPassword))
            mqttConfigurationViewModel.onEvent(UpdateMqttPort(mqttPort.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttRetryInterval(mqttRetryInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttUserName(mqttUserName))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeyStoreFile(mqttKeyStoreFile!!))
        }

        assertEquals(mqttConnectionData, mqttConfigurationViewModel.viewState.value.editData)

        mqttConfigurationViewModel.onEvent(Save)

        assertEquals(mqttConnectionData, mqttConfigurationViewModel.viewState.value.editData)
        assertEquals(mqttConnectionData, MqttConnectionData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(
            initialMqttConnectionData,
            mqttConfigurationViewModel.viewState.value.editData
        )

        with(mqttConnectionData) {
            mqttConfigurationViewModel.onEvent(SetMqttEnabled(isMqttEnabled))
            mqttConfigurationViewModel.onEvent(SetMqttSSLEnabled(isMqttSSLEnabled))
            mqttConfigurationViewModel.onEvent(UpdateMqttConnectionTimeout(mqttConnectionTimeout.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttHost(mqttHost))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeepAliveInterval(mqttKeepAliveInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttPassword(mqttPassword))
            mqttConfigurationViewModel.onEvent(UpdateMqttPort(mqttPort.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttRetryInterval(mqttRetryInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttUserName(mqttUserName))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeyStoreFile(mqttKeyStoreFile!!))
        }

        assertEquals(mqttConnectionData, mqttConfigurationViewModel.viewState.value.editData)

        mqttConfigurationViewModel.onEvent(Discard)

        assertEquals(
            initialMqttConnectionData,
            mqttConfigurationViewModel.viewState.value.editData
        )
        assertEquals(initialMqttConnectionData, MqttConnectionData())
    }
}