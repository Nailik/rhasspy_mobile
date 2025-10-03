package org.rhasspy.mobile.viewmodel.configuration.mqtt

import kotlinx.coroutines.test.runTest
import okio.Path
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.SetMqttEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.SetMqttSSLEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttConnectionTimeout
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttHost
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttKeepAliveInterval
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttKeyStoreFile
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttKeyStorePassword
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttPassword
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttPort
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttRetryInterval
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttUserName
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationViewState.MqttConfigurationData
import org.rhasspy.mobile.viewmodel.getRandomString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MqttConfigurationViewModelTest : AppTest() {

    private lateinit var mqttConfigurationViewModel: MqttConfigurationViewModel

    private lateinit var initialMqttConfigurationData: MqttConfigurationData
    private lateinit var mqttConfigurationData: MqttConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        initialMqttConfigurationData = MqttConfigurationData(
            isMqttEnabled = false,
            mqttHost = "",
            mqttPort = 1883,
            mqttUserName = "",
            mqttPassword = "",
            isMqttSSLEnabled = false,
            mqttConnectionTimeout = 5L,
            mqttKeepAliveInterval = 30L,
            mqttRetryInterval = 10L,
            mqttKeyStoreFile = null,
            isKeyStoreFileTextVisible = false,
            mqttKeyStorePassword = ""
        )

        mqttConfigurationData = MqttConfigurationData(
            isMqttEnabled = true,
            mqttHost = getRandomString(5),
            mqttPort = 1652,
            mqttUserName = getRandomString(5),
            mqttPassword = getRandomString(5),
            isMqttSSLEnabled = true,
            mqttConnectionTimeout = 6914L,
            mqttKeepAliveInterval = 9154L,
            mqttRetryInterval = 1958L,
            mqttKeyStoreFile = Path.commonInternalPath(get(), getRandomString(5)),
            isKeyStoreFileTextVisible = true,
            mqttKeyStorePassword = getRandomString(5)
        )

        mqttConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(
            initialMqttConfigurationData,
            mqttConfigurationViewModel.viewState.value.editData
        )

        with(mqttConfigurationData) {
            mqttConfigurationViewModel.onEvent(SetMqttEnabled(isMqttEnabled))
            mqttConfigurationViewModel.onEvent(SetMqttSSLEnabled(isMqttSSLEnabled))
            mqttConfigurationViewModel.onEvent(UpdateMqttConnectionTimeout(mqttConnectionTimeout.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttHost(mqttHost))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeepAliveInterval(mqttKeepAliveInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttPassword(mqttPassword))
            mqttConfigurationViewModel.onEvent(UpdateMqttPort(mqttPort.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttRetryInterval(mqttRetryInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttUserName(mqttUserName))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeyStoreFile(mqttKeyStoreFile!!)),
            mqttConfigurationViewModel.onEvent(UpdateMqttKeyStorePassword(mqttKeyStorePassword))
        }

        assertEquals(mqttConfigurationData, mqttConfigurationViewModel.viewState.value.editData)

        mqttConfigurationViewModel.onEvent(Save)

        assertEquals(mqttConfigurationData, mqttConfigurationViewModel.viewState.value.editData)
        assertEquals(mqttConfigurationData, MqttConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(
            initialMqttConfigurationData,
            mqttConfigurationViewModel.viewState.value.editData
        )

        with(mqttConfigurationData) {
            mqttConfigurationViewModel.onEvent(SetMqttEnabled(isMqttEnabled))
            mqttConfigurationViewModel.onEvent(SetMqttSSLEnabled(isMqttSSLEnabled))
            mqttConfigurationViewModel.onEvent(UpdateMqttConnectionTimeout(mqttConnectionTimeout.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttHost(mqttHost))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeepAliveInterval(mqttKeepAliveInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttPassword(mqttPassword))
            mqttConfigurationViewModel.onEvent(UpdateMqttPort(mqttPort.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttRetryInterval(mqttRetryInterval.toString()))
            mqttConfigurationViewModel.onEvent(UpdateMqttUserName(mqttUserName))
            mqttConfigurationViewModel.onEvent(UpdateMqttKeyStoreFile(mqttKeyStoreFile!!)),
            mqttConfigurationViewModel.onEvent(UpdateMqttKeyStorePassword(mqttKeyStorePassword))
        }

        assertEquals(mqttConfigurationData, mqttConfigurationViewModel.viewState.value.editData)

        mqttConfigurationViewModel.onEvent(Discard)

        assertEquals(
            initialMqttConfigurationData,
            mqttConfigurationViewModel.viewState.value.editData
        )
        assertEquals(initialMqttConfigurationData, MqttConfigurationData())
    }
}