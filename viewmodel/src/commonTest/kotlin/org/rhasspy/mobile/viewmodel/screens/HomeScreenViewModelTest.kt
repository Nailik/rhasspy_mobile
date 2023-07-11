package org.rhasspy.mobile.viewmodel.screens

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.dsl.module
import org.koin.test.get
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.PlayStopRecording
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.coEvery
import org.rhasspy.mobile.viewmodel.coVerify
import org.rhasspy.mobile.viewmodel.nVerify
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.MicrophoneFabClick
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.TogglePlayRecording
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test

class HomeScreenViewModelTest : AppTest() {

    @Mock
    lateinit var serviceMiddleware: IServiceMiddleware

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    private lateinit var homeScreenViewModel: HomeScreenViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { serviceMiddleware }
                single { microphonePermission }
            }
        )

        every { serviceMiddleware.isUserActionEnabled } returns MutableStateFlow(true).readOnly
        every { serviceMiddleware.isPlayingRecording } returns MutableStateFlow(false).readOnly
        every { serviceMiddleware.isPlayingRecordingEnabled } returns MutableStateFlow(false).readOnly
    }

    @Test
    fun `when user clicks play recording it's toggled`() {
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly
        every { serviceMiddleware.action(isAny()) } returns Unit

        homeScreenViewModel = get()
        homeScreenViewModel.onEvent(TogglePlayRecording)

        nVerify { serviceMiddleware.action(PlayStopRecording) }
    }

    @Test
    fun `when user clicks microphone fab and no microphone permission is given it's requested`() = runTest {
        coEvery { microphonePermission.request() } returns Unit
        every { microphonePermission.shouldShowInformationDialog() } returns false
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly

        homeScreenViewModel = get()
        homeScreenViewModel.onEvent(MicrophoneFabClick)

        coVerify { microphonePermission.request() }
    }

    @Test
    fun `when user clicks microphone fab and microphone permission is given session is toggled`() {
        every { serviceMiddleware.userSessionClick() } returns Unit
        every { microphonePermission.shouldShowInformationDialog() } returns false
        every { microphonePermission.granted } returns MutableStateFlow(true).readOnly

        homeScreenViewModel = get()
        homeScreenViewModel.onEvent(MicrophoneFabClick)

        nVerify { serviceMiddleware.userSessionClick() }
    }

}