package org.rhasspy.mobile.viewmodel.configuration.mqtt

import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.viewmodel.AppTest
import kotlin.test.BeforeTest

class MqttConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var nativeApplication: INativeApplication
    override fun setUpMocks() = injectMocks(mocker)

    private lateinit var mqttConfigurationViewModel: MqttConfigurationViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        mqttConfigurationViewModel = get()
    }
/*
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
    */

}