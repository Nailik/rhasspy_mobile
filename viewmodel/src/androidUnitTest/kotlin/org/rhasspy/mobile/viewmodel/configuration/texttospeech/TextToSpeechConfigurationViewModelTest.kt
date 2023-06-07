package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.edit.texttospeech.TextToSpeechConfigurationEditViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test

class TextToSpeechConfigurationViewModelTest : AppTest() {

    private lateinit var textToSpeechConfigurationViewModel: TextToSpeechConfigurationEditViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        textToSpeechConfigurationViewModel = get()
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