package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewState.AudioPlayingConfigurationData
import org.rhasspy.mobile.viewmodel.getRandomString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AudioPlayingConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var nativeApplication: INativeApplication

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    @Mock
    lateinit var overlayPermission: IOverlayPermission

    override fun setUpMocks() = injectMocks(mocker)

    private lateinit var audioPlayingConfigurationViewModel: AudioPlayingConfigurationViewModel

    private lateinit var initialAudioPlayingConfigurationData: AudioPlayingConfigurationData
    private lateinit var audioPlayingConfigurationData: AudioPlayingConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { nativeApplication }
                single { microphonePermission }
                single { overlayPermission }
            }
        )

        initialAudioPlayingConfigurationData = AudioPlayingConfigurationData(
            audioPlayingOption = AudioPlayingOption.Local,
            audioOutputOption = AudioOutputOption.Sound,
            isUseCustomAudioPlayingHttpEndpoint = false,
            audioPlayingHttpEndpoint = "",
            audioPlayingMqttSiteId = ""
        )

        audioPlayingConfigurationData = AudioPlayingConfigurationData(
            audioPlayingOption = AudioPlayingOption.RemoteMQTT,
            audioOutputOption = AudioOutputOption.Notification,
            isUseCustomAudioPlayingHttpEndpoint = true,
            audioPlayingHttpEndpoint = getRandomString(5),
            audioPlayingMqttSiteId = getRandomString(5)
        )

        audioPlayingConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialAudioPlayingConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)

        with(audioPlayingConfigurationData) {
            audioPlayingConfigurationViewModel.onEvent(SelectEditAudioPlayingOption(audioPlayingOption))
            audioPlayingConfigurationViewModel.onEvent(SelectAudioOutputOption(audioOutputOption))
            audioPlayingConfigurationViewModel.onEvent(SetUseCustomHttpEndpoint(isUseCustomAudioPlayingHttpEndpoint))
            audioPlayingConfigurationViewModel.onEvent(ChangeEditAudioPlayingHttpEndpoint(audioPlayingHttpEndpoint))
            audioPlayingConfigurationViewModel.onEvent(ChangeEditAudioPlayingMqttSiteId(audioPlayingMqttSiteId))
        }

        assertEquals(audioPlayingConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)

        audioPlayingConfigurationViewModel.onEvent(Save)

        assertEquals(audioPlayingConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)
        assertEquals(audioPlayingConfigurationData, AudioPlayingConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialAudioPlayingConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)

        with(audioPlayingConfigurationData) {
            audioPlayingConfigurationViewModel.onEvent(SelectEditAudioPlayingOption(audioPlayingOption))
            audioPlayingConfigurationViewModel.onEvent(SelectAudioOutputOption(audioOutputOption))
            audioPlayingConfigurationViewModel.onEvent(SetUseCustomHttpEndpoint(isUseCustomAudioPlayingHttpEndpoint))
            audioPlayingConfigurationViewModel.onEvent(ChangeEditAudioPlayingHttpEndpoint(audioPlayingHttpEndpoint))
            audioPlayingConfigurationViewModel.onEvent(ChangeEditAudioPlayingMqttSiteId(audioPlayingMqttSiteId))
        }

        assertEquals(audioPlayingConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)

        audioPlayingConfigurationViewModel.onEvent(Discard)

        assertEquals(initialAudioPlayingConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)
        assertEquals(initialAudioPlayingConfigurationData, AudioPlayingConfigurationData())
    }

}