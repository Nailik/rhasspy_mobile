package org.rhasspy.mobile.viewmodel.settings

import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action.ToggleAudioLevelTest
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Change.UpdateSilenceDetectionAudioLevelLogarithm
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsViewModel
import kotlin.math.pow
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SilenceDetectionSettingsViewModelTest : AppTest() {

    @RelaxedMockK
    lateinit var nativeApplication: NativeApplication

    @RelaxedMockK
    lateinit var audioRecorder: AudioRecorder

    private lateinit var silenceDetectionSettingsViewModel: SilenceDetectionSettingsViewModel

    private val isAppInBackground = MutableStateFlow(false)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { nativeApplication }
                single { audioRecorder }
            }
        )

        every { nativeApplication.isAppInBackground } returns isAppInBackground
        every { audioRecorder.maxVolume } returns MutableStateFlow(0f)
        every { audioRecorder.absoluteMaxVolume } returns 100f
        every { audioRecorder.isRecording } returns MutableStateFlow(false)

        silenceDetectionSettingsViewModel = get()
    }

    @Test
    fun `when the user updates the audio level logarithm it's correctly updated`() {
        silenceDetectionSettingsViewModel.onEvent(UpdateSilenceDetectionAudioLevelLogarithm(0f))
        assertEquals(0f, AppSetting.automaticSilenceDetectionAudioLevel.value)

        arrayOf(0.25f, 0.5f, 0.75f, 1f).forEach { percentage ->
            silenceDetectionSettingsViewModel.onEvent(UpdateSilenceDetectionAudioLevelLogarithm(percentage))
            assertEquals(audioRecorder.absoluteMaxVolume.pow(percentage), AppSetting.automaticSilenceDetectionAudioLevel.value)
        }

    }

    @Test
    fun `when the user tests the audio recording and closes the app audio recording test is stopped`() {
        assertEquals(false, audioRecorder.isRecording.value)

        silenceDetectionSettingsViewModel.onEvent(ToggleAudioLevelTest)
        verify {
            audioRecorder.startRecording(
                AppSetting.audioRecorderSampleRate.value,
                AppSetting.audioRecorderChannel.value,
                AppSetting.audioRecorderEncoding.value,
            )
        }

        isAppInBackground.value = true
        verify(timeout = 1000) { audioRecorder.stopRecording() }
    }

}