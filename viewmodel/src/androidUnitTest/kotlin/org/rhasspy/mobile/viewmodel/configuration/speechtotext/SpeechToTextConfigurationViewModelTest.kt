package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SpeechToTextConfigurationViewModelTest : AppTest() {

    private lateinit var speechToTextConfigurationViewModel: SpeechToTextConfigurationViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        speechToTextConfigurationViewModel = get()
    }

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
}