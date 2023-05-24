package org.rhasspy.mobile.viewmodel.screens

import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.get
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.PlayStopRecording
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.MicrophoneFabClick
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.TogglePlayRecording
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewModel
import kotlin.test.BeforeTest

class HomeScreenViewModelTest : AppTest() {

    @RelaxedMockK
    lateinit var serviceMiddleware: ServiceMiddleware

    @RelaxedMockK
    lateinit var microphonePermission: MicrophonePermission

    private lateinit var homeScreenViewModel: HomeScreenViewModel

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
        homeScreenViewModel = get()
    }

    @Test
    fun `when user clicks play recording it's toggled`() {
        homeScreenViewModel.onEvent(TogglePlayRecording)

        verify { serviceMiddleware.action(PlayStopRecording) }
    }

    @Test
    fun `when user clicks microphone fab and no microphone permission is given it's requested`() {
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly

        homeScreenViewModel.onEvent(MicrophoneFabClick)
        coVerify { microphonePermission.request() }
    }

    @Test
    fun `when user clicks microphone fab and microphone permission is given session is toggled`() {
        every { microphonePermission.granted } returns MutableStateFlow(true).readOnly

        homeScreenViewModel.onEvent(MicrophoneFabClick)
        verify { serviceMiddleware.userSessionClick() }
    }

}