package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.edit.intentrecognition.IntentRecognitionConfigurationEditViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test

class IntentRecognitionConfigurationViewModelTest : AppTest() {

    private lateinit var intentRecognitionConfigurationViewModel: IntentRecognitionConfigurationEditViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        intentRecognitionConfigurationViewModel = get()
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