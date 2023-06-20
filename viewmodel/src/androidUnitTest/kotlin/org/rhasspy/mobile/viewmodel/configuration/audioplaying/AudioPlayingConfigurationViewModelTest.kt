package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test

class AudioPlayingConfigurationViewModelTest : AppTest() {

    private lateinit var audioPlayingConfigurationViewModel: AudioPlayingConfigurationViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        audioPlayingConfigurationViewModel = get()
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

    @Test
    fun getScreen() {
    }
}