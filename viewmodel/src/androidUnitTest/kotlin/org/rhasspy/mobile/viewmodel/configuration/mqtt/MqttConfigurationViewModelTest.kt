package org.rhasspy.mobile.viewmodel.configuration.mqtt

import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class MqttConfigurationViewModelTest : AppTest() {

    private lateinit var mqttConfigurationViewModel: MqttConfigurationViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        mqttConfigurationViewModel = get()
    }

    @Test
    fun getScreen() {
    }

    @Test
    fun onEvent() {
    }

    @Test
    fun onSave() {
    }

    @Test
    fun onDiscard() {
    }
}