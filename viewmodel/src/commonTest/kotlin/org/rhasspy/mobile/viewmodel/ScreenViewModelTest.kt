package org.rhasspy.mobile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Dialog.Confirm
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.MicrophonePermissionInfo
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.OverlayPermissionInfo
import kotlin.test.*

class ScreenViewModelTest : AppTestNew() {

    @Mock
    lateinit var navigator: INavigator

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    @Mock
    lateinit var overlayPermission: IOverlayPermission


    private lateinit var screenViewModel: ScreenViewModel

    override fun setUpMocks() = injectMocks(mocker)

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
        every { navigator.onComposed(isAny()) } returns Unit

        screenViewModel.onComposed()
        verify { navigator.onComposed(screenViewModel) }
    }

    @Test
    fun `when a view model is disposed it informs the navigator`() {
        every { navigator.onDisposed(isAny()) } returns Unit
        screenViewModel.onDisposed()
        verify { navigator.onDisposed(screenViewModel) }
    }

    @Test
    fun `when the microphone permission is required but not given it's requested`() = runTest {
        everySuspending { microphonePermission.request() } returns Unit
        every { microphonePermission.shouldShowInformationDialog() } returns false
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly

        screenViewModel.requireMicrophonePermission { }

        verifyWithSuspend(exhaustive = false, inOrder = false) { microphonePermission.request() }
    }

    @Test
    fun `when the microphone permission is required abd given the function is executed`() = runTest {
        var wasCalled = false
        every { microphonePermission.granted } returns MutableStateFlow(true).readOnly

        screenViewModel.requireMicrophonePermission {
            wasCalled = true
        }

        verifyWithSuspend(exhaustive = false, inOrder = false) { repeat(0) { microphonePermission.request() } }
        assertTrue(wasCalled)
    }

    @Test
    fun `when microphone permission dialog is shown onBackPressClick doesn't close screen`() = runTest {
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly
        every { microphonePermission.shouldShowInformationDialog() } returns true

        screenViewModel.requireMicrophonePermission { }

        assertEquals(MicrophonePermissionInfo, screenViewModel.screenViewState.value.dialogState)

        screenViewModel.onBackPressedClick()

        assertNull(screenViewModel.screenViewState.value.dialogState)
        verifyWithSuspend(exhaustive = false, inOrder = false) { repeat(0) { navigator.onBackPressed() } }
    }

    @Test
    fun `when the overlay permission is required but not given it's requested`() = runTest {
        every { overlayPermission.request() } returns true
        every { overlayPermission.granted } returns MutableStateFlow(false).readOnly

        screenViewModel.requireOverlayPermission { }
        screenViewModel.onEvent(Confirm(OverlayPermissionInfo))

        verifyWithSuspend(exhaustive = false, inOrder = false) { overlayPermission.request() }
    }

    @Test
    fun `when the overlay permission is required abd given the function is executed`() = runTest {
        var wasCalled = false
        every { overlayPermission.granted } returns MutableStateFlow(true).readOnly

        screenViewModel.requireOverlayPermission {
            wasCalled = true
        }

        verifyWithSuspend(exhaustive = false, inOrder = false) { repeat(0) { overlayPermission.request() } }
        assertTrue(wasCalled)
    }

    @Test
    fun `when overlay permission dialog is shown onBackPressClick doesn't close screen`() = runTest {
        every { navigator.onBackPressed() } returns Unit
        every { overlayPermission.granted } returns MutableStateFlow(false).readOnly
        screenViewModel.requireOverlayPermission { }

        assertEquals(OverlayPermissionInfo, screenViewModel.screenViewState.value.dialogState)

        screenViewModel.onBackPressedClick()

        assertNull(screenViewModel.screenViewState.value.dialogState)
        verifyWithSuspend(exhaustive = false, inOrder = false) { repeat(0) { navigator.onBackPressed() } }
    }

}