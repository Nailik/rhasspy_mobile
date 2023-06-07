package org.rhasspy.mobile.viewmodel.configuration.wakeword

import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationEditViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test

class WakeWordConfigurationViewModelTest : AppTest() {

    private lateinit var wakeWordConfigurationViewModel: WakeWordConfigurationEditViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        wakeWordConfigurationViewModel = get()
    }

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
}