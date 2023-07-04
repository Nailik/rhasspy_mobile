package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.viewmodel.AppTest
import kotlin.test.BeforeTest

class SpeechToTextConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var nativeApplication: INativeApplication
    override fun setUpMocks() = injectMocks(mocker)

    private lateinit var speechToTextConfigurationViewModel: SpeechToTextConfigurationViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        speechToTextConfigurationViewModel = get()
    }
/*
    @Test
    fun getScreen() {
    }

    @Test
    fun onEvent() {
    }

    @Test
    fun onDiscard() {
    }

    @Test
    fun onSave() {
    }

 */
}