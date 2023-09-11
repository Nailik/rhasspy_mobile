package org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.nVerify
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.LocalSilenceDetectionUiEvent.Action.ToggleAudioLevelTest
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.LocalSilenceDetectionUiEvent.Change.UpdateSilenceDetectionAudioLevelLogarithm
import org.rhasspy.mobile.viewmodel.settings.injectMocks
import kotlin.math.pow
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class VoiceActivityDetectionConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var audioRecorder: IAudioRecorder

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    @Mock
    lateinit var overlayPermission: IOverlayPermission


    private lateinit var voiceActivityDetectionConfigurationViewModel: VoiceActivityDetectionConfigurationViewModel

    private val isAppInBackground = MutableStateFlow(false)

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { audioRecorder }
                single { microphonePermission }
                single { overlayPermission }
            }
        )

        every { audioRecorder.maxVolume } returns MutableStateFlow(0f)
        every { audioRecorder.absoluteMaxVolume } returns 100f
        every { audioRecorder.isRecording } returns MutableStateFlow(false)
        every { audioRecorder.stopRecording() } returns Unit

        voiceActivityDetectionConfigurationViewModel = get()
    }

    @Test
    fun `when the user updates the audio level logarithm it's correctly updated`() = runTest {
        voiceActivityDetectionConfigurationViewModel.onEvent(UpdateSilenceDetectionAudioLevelLogarithm(0f))
        voiceActivityDetectionConfigurationViewModel.onEvent(Save)
        assertEquals(0f, ConfigurationSetting.automaticSilenceDetectionAudioLevel.value)

        arrayOf(0.25f, 0.5f, 0.75f, 1f).forEach { percentage ->
            voiceActivityDetectionConfigurationViewModel.onEvent(UpdateSilenceDetectionAudioLevelLogarithm(percentage))
            voiceActivityDetectionConfigurationViewModel.onEvent(Save)
            assertEquals(audioRecorder.absoluteMaxVolume.pow(percentage), ConfigurationSetting.automaticSilenceDetectionAudioLevel.value)
        }
    }

    //Manual @Test
    @Suppress("unused")
    fun `when the user tests the audio recording and closes the app audio recording test is stopped`() {
        every { audioRecorder.stopRecording() } returns Unit
        every { audioRecorder.startRecording(isAny(), isAny(), isAny(), isAny(), isAny(), isAny(), isAny()) } returns Unit
        every { microphonePermission.granted } returns MutableStateFlow(true)
        assertEquals(false, audioRecorder.isRecording.value)

        voiceActivityDetectionConfigurationViewModel.onEvent(ToggleAudioLevelTest)
        nVerify {
            audioRecorder.startRecording(isAny(), isAny(), isAny(), isAny(), isAny(), isAny(), isAny())
        }

        isAppInBackground.value = true
        nVerify { audioRecorder.stopRecording() }
    }

}