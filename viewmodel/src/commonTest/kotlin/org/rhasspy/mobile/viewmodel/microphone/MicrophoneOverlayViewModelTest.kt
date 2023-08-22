package org.rhasspy.mobile.viewmodel.microphone

import kotlinx.coroutines.flow.MutableStateFlow
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.platformspecific.intent.IIntentAction
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.nVerify
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Action.ToggleUserSession
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Change.UpdateMicrophoneOverlayPosition
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayViewModel
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MicrophoneOverlayViewModelTest : AppTest() {

    @Mock
    lateinit var serviceMiddleware: IServiceMiddleware

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    @Mock
    lateinit var overlayPermission: IOverlayPermission

    @Mock
    lateinit var intentAction: IIntentAction


    private lateinit var microphoneOverlayViewModel: MicrophoneOverlayViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { serviceMiddleware }
                single { microphonePermission }
                single { overlayPermission }
                single { intentAction }
            }
        )

        every { serviceMiddleware.isUserActionEnabled } returns MutableStateFlow(true).readOnly
        every { serviceMiddleware.isPlayingRecording } returns MutableStateFlow(false).readOnly
        every { serviceMiddleware.isPlayingRecordingEnabled } returns MutableStateFlow(false).readOnly
        every { overlayPermission.granted } returns MutableStateFlow(false).readOnly
    }

    @Test
    fun `when user moves overlay position is updated by offset`() {
        every { microphonePermission.granted } returns MutableStateFlow(true).readOnly

        val xBefore = AppSetting.microphoneOverlayPositionX.value
        val yBefore = AppSetting.microphoneOverlayPositionY.value

        val xDiff = Random.nextFloat()
        val yDiff = Random.nextFloat()

        microphoneOverlayViewModel = get()
        microphoneOverlayViewModel.onEvent(UpdateMicrophoneOverlayPosition(xDiff, yDiff))

        assertEquals((xBefore + xDiff).toInt(), AppSetting.microphoneOverlayPositionX.value)
        assertEquals((yBefore + yDiff).toInt(), AppSetting.microphoneOverlayPositionY.value)
    }

    @Test
    fun `when user clicks overlay and microphone permission is granted user session is started`() {
        every { serviceMiddleware.userSessionClick() } returns Unit
        every { microphonePermission.granted } returns MutableStateFlow(true).readOnly

        microphoneOverlayViewModel = get()
        microphoneOverlayViewModel.onEvent(ToggleUserSession)

        nVerify { serviceMiddleware.userSessionClick() }
    }

    @Test
    fun `when user clicks overlay and microphone permission is not granted application is started and permission is requested by HomeScreenViewModel`() {
        every { intentAction.startRecording() } returns Unit
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly

        microphoneOverlayViewModel = get()
        microphoneOverlayViewModel.onEvent(ToggleUserSession)

        nVerify { intentAction.startRecording() }
    }

}