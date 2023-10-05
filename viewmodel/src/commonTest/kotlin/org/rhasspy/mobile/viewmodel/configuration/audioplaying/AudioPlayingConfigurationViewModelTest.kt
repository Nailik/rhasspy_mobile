package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.SndDomainOption
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.getRandomString
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Discard
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.domains.snd.AudioPlayingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.domains.snd.AudioPlayingConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.snd.AudioPlayingConfigurationViewState.SndDomainConfigurationData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AudioPlayingConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    @Mock
    lateinit var overlayPermission: IOverlayPermission

    override fun setUpMocks() = injectMocks(mocker)

    private lateinit var audioPlayingConfigurationViewModel: AudioPlayingConfigurationViewModel

    private lateinit var initialSndDomainConfigurationData: SndDomainConfigurationData
    private lateinit var sndDomainConfigurationData: SndDomainConfigurationData

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { microphonePermission }
                single { overlayPermission }
            }
        )

        initialSndDomainConfigurationData = SndDomainConfigurationData(
            sndDomainOption = SndDomainOption.Local,
            audioOutputOption = AudioOutputOption.Sound,
            audioPlayingMqttSiteId = ""
        )

        sndDomainConfigurationData = SndDomainConfigurationData(
            sndDomainOption = SndDomainOption.Rhasspy2HermesMQTT,
            audioOutputOption = AudioOutputOption.Notification,
            audioPlayingMqttSiteId = getRandomString(5)
        )

        audioPlayingConfigurationViewModel = get()
    }

    @Test
    fun `when data is changed it's updated and on save it's saved`() = runTest {
        assertEquals(initialSndDomainConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)

        with(sndDomainConfigurationData) {
            audioPlayingConfigurationViewModel.onEvent(SelectEditAudioPlayingOption(sndDomainOption))
            audioPlayingConfigurationViewModel.onEvent(SelectAudioOutputOption(audioOutputOption))
            audioPlayingConfigurationViewModel.onEvent(ChangeEditAudioPlayingMqttSiteId(audioPlayingMqttSiteId))
        }

        assertEquals(sndDomainConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)

        audioPlayingConfigurationViewModel.onEvent(Save)

        assertEquals(sndDomainConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)
        assertEquals(sndDomainConfigurationData, SndDomainConfigurationData())
    }

    @Test
    fun `when data is changed it's updated and on discard it's discarded`() = runTest {
        assertEquals(initialSndDomainConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)

        with(sndDomainConfigurationData) {
            audioPlayingConfigurationViewModel.onEvent(SelectEditAudioPlayingOption(sndDomainOption))
            audioPlayingConfigurationViewModel.onEvent(SelectAudioOutputOption(audioOutputOption))
            audioPlayingConfigurationViewModel.onEvent(ChangeEditAudioPlayingMqttSiteId(audioPlayingMqttSiteId))
        }

        assertEquals(sndDomainConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)

        audioPlayingConfigurationViewModel.onEvent(Discard)

        assertEquals(initialSndDomainConfigurationData, audioPlayingConfigurationViewModel.viewState.value.editData)
        assertEquals(initialSndDomainConfigurationData, SndDomainConfigurationData())
    }

}