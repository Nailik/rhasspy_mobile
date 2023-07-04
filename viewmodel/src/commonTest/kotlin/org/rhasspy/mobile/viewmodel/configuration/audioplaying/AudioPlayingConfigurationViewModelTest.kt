package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.viewmodel.AppTest
import kotlin.test.BeforeTest

class AudioPlayingConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var nativeApplication: INativeApplication

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    @Mock
    lateinit var overlayPermission: IOverlayPermission

    override fun setUpMocks() = injectMocks(mocker)

    private lateinit var audioPlayingConfigurationViewModel: AudioPlayingConfigurationViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { nativeApplication }
                single { microphonePermission }
                single { overlayPermission }
            }
        )

        audioPlayingConfigurationViewModel = get()
    }
    /*
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

     */
}