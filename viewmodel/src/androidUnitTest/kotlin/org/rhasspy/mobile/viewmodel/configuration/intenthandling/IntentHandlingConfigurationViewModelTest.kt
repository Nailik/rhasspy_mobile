package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationEditViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test

class IntentHandlingConfigurationViewModelTest : AppTest() {

    private lateinit var intentHandlingConfigurationViewModel: IntentHandlingConfigurationEditViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        intentHandlingConfigurationViewModel = get()
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