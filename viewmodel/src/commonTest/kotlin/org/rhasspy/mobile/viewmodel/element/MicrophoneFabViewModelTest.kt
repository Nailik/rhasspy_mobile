package org.rhasspy.mobile.viewmodel.element

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.dsl.module
import org.koin.test.get
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.coVerify
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabUiEvent.Action.MicrophoneFabClick
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.nVerify
import kotlin.test.BeforeTest
import kotlin.test.Test

class MicrophoneFabViewModelTest : AppTest() {

    @Mock
    lateinit var serviceMiddleware: IServiceMiddleware

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    private lateinit var microphoneFabViewModel: MicrophoneFabViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { serviceMiddleware }
                single { microphonePermission }
            }
        )

        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly
        every { serviceMiddleware.isUserActionEnabled } returns MutableStateFlow(true).readOnly
        every { serviceMiddleware.isPlayingRecording } returns MutableStateFlow(false).readOnly
        every { serviceMiddleware.isPlayingRecordingEnabled } returns MutableStateFlow(false).readOnly
        microphoneFabViewModel = get()
    }

    @Test
    fun `when user clicks microphone fab and no microphone permission is given it's requested`() = runTest {
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly

        microphoneFabViewModel.onEvent(MicrophoneFabClick)
        coVerify { microphonePermission.request() }
    }

    @Test
    fun `when user clicks microphone fab and microphone permission is given session is toggled`() {
        every { microphonePermission.granted } returns MutableStateFlow(true).readOnly

        microphoneFabViewModel.onEvent(MicrophoneFabClick)
        nVerify { serviceMiddleware.userSessionClick() }
    }

}