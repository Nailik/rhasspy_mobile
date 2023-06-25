package org.rhasspy.mobile.viewmodel

import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Dialog.Confirm
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.MicrophonePermissionInfo
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.OverlayPermissionInfo
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScreenViewModelTest : AppTest() {

    @RelaxedMockK
    lateinit var navigator: Navigator

    @RelaxedMockK
    lateinit var microphonePermission: MicrophonePermission

    @RelaxedMockK
    lateinit var overlayPermission: OverlayPermission

    @RelaxedMockK
    lateinit var function: () -> Unit

    private lateinit var screenViewModel: ScreenViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { navigator }
                single { microphonePermission }
                single { overlayPermission }
            }
        )

        screenViewModel = object : ScreenViewModel() {}
    }

    @Test
    fun `when a view model is composed it informs the navigator`() {
        screenViewModel.onComposed()
        verify { navigator.onComposed(screenViewModel) }
    }

    @Test
    fun `when a view model is disposed it informs the navigator`() {
        screenViewModel.onDisposed()
        verify { navigator.onDisposed(screenViewModel) }
    }

    @Test
    fun `when the microphone permission is required but not given it's requested`() {
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly

        screenViewModel.requireMicrophonePermission(function)

        coVerify { microphonePermission.request() }
    }

    @Test
    fun `when the microphone permission is required abd given the function is executed`() {
        every { microphonePermission.granted } returns MutableStateFlow(true).readOnly

        screenViewModel.requireMicrophonePermission(function)

        coVerify(exactly = 0) { microphonePermission.request() }
        verify { function() }
    }

    @Test
    fun `when microphone permission dialog is shown onBackPressClick doesn't close screen`() {
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly
        every { microphonePermission.shouldShowInformationDialog() } returns true

        screenViewModel.requireMicrophonePermission(function)

        assertEquals(MicrophonePermissionInfo, screenViewModel.screenViewState.value.dialogState)

        screenViewModel.onBackPressedClick()

        assertNull(screenViewModel.screenViewState.value.dialogState)
        coVerify(exactly = 0) { navigator.onBackPressed() }
    }

    @Test
    fun `when the overlay permission is required but not given it's requested`() {
        every { overlayPermission.granted } returns MutableStateFlow(false).readOnly

        screenViewModel.requireOverlayPermission(function)
        screenViewModel.onEvent(Confirm(OverlayPermissionInfo))

        coVerify { overlayPermission.request() }
    }

    @Test
    fun `when the overlay permission is required abd given the function is executed`() {
        every { overlayPermission.granted } returns MutableStateFlow(true).readOnly

        screenViewModel.requireOverlayPermission(function)

        coVerify(exactly = 0) { overlayPermission.request() }
        verify { function() }
    }

    @Test
    fun `when overlay permission dialog is shown onBackPressClick doesn't close screen`() {
        every { overlayPermission.granted } returns MutableStateFlow(false).readOnly
        screenViewModel.requireOverlayPermission(function)

        assertEquals(OverlayPermissionInfo, screenViewModel.screenViewState.value.dialogState)

        screenViewModel.onBackPressedClick()

        assertNull(screenViewModel.screenViewState.value.dialogState)
        coVerify(exactly = 0) { navigator.onBackPressed() }
    }

}