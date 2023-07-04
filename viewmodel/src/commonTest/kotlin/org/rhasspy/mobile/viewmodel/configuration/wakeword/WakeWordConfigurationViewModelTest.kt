package org.rhasspy.mobile.viewmodel.configuration.wakeword

import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.viewmodel.AppTest
import kotlin.test.BeforeTest

class WakeWordConfigurationViewModelTest : AppTest() {
    @Mock
    lateinit var nativeApplication: INativeApplication
    override fun setUpMocks() = injectMocks(mocker)

    private lateinit var wakeWordConfigurationViewModel: WakeWordConfigurationViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        wakeWordConfigurationViewModel = get()
    }
/*
    @Test
    fun getScreen() {
    }

    @Test
    fun getPorcupineScreen() {
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

    @Test
    fun onBackPressed() {
    }

 */
}