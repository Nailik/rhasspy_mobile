package org.rhasspy.mobile.viewmodel.settings

import kotlinx.coroutines.flow.MutableStateFlow
import org.kodein.mock.Mock
import org.kodein.mock.generated.injectMocks
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.nVerify
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action.ToggleAudioLevelTest
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Change.UpdateSilenceDetectionAudioLevelLogarithm
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsViewModel
import kotlin.math.pow
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SilenceDetectionSettingsViewModelTest : AppTest() {

    @Mock
    lateinit var audioRecorder: IAudioRecorder

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    @Mock
    lateinit var overlayPermission: IOverlayPermission

    private lateinit var silenceDetectionSettingsViewModel: SilenceDetectionSettingsViewModel

    private val isAppInBackground = MutableStateFlow(false)

    override fun setUpMocks() = mocker.injectMocks(this)

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

        silenceDetectionSettingsViewModel = get()
    }

    @Test
    fun `when the user updates the audio level logarithm it's correctly updated`() {
        silenceDetectionSettingsViewModel.onEvent(UpdateSilenceDetectionAudioLevelLogarithm(0f))
        assertEquals(0f, AppSetting.automaticSilenceDetectionAudioLevel.value)

        arrayOf(0.25f, 0.5f, 0.75f, 1f).forEach { percentage ->
            silenceDetectionSettingsViewModel.onEvent(
                UpdateSilenceDetectionAudioLevelLogarithm(
                    percentage
                )
            )
            assertEquals(
                audioRecorder.absoluteMaxVolume.pow(percentage),
                AppSetting.automaticSilenceDetectionAudioLevel.value
            )
        }
    }

    //Manual @Test
    @Suppress("unused")
    fun `when the user tests the audio recording and closes the app audio recording test is stopped`() {
        every { audioRecorder.stopRecording() } returns Unit
        every {
            audioRecorder.startRecording(
                isAny(),
                isAny(),
                isAny(),
                isAny(),
                isAny(),
                isAny(),
                isAny()
            )
        } returns Unit
        every { microphonePermission.granted } returns MutableStateFlow(true)
        assertEquals(false, audioRecorder.isRecording.value)

        silenceDetectionSettingsViewModel.onEvent(ToggleAudioLevelTest)
        nVerify {
            audioRecorder.startRecording(
                isAny(),
                isAny(),
                isAny(),
                isAny(),
                isAny(),
                isAny(),
                isAny()
            )
        }

        isAppInBackground.value = true
        nVerify { audioRecorder.stopRecording() }
    }

}