package org.rhasspy.mobile.viewmodel.screen

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.kodein.mock.generated.injectMocks
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.coVerify
import org.rhasspy.mobile.viewmodel.nVerify
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Dialog.Confirm
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.MicrophonePermissionInfo
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.OverlayPermissionInfo
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ScreenViewModelTest : AppTest() {

    @Mock
    lateinit var navigator: INavigator

    @Mock
    lateinit var microphonePermission: IMicrophonePermission

    @Mock
    lateinit var overlayPermission: IOverlayPermission

    private lateinit var screenViewModel: ScreenViewModel

    override fun setUpMocks() = mocker.injectMocks(this)

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
        nVerify { navigator.onComposed(screenViewModel) }
    }

    @Test
    fun `when a view model is disposed it informs the navigator`() {
        every { navigator.onDisposed(isAny()) } returns Unit
        screenViewModel.onDisposed()
        nVerify { navigator.onDisposed(screenViewModel) }
    }

    @Test
    fun `when the microphone permission is required but not given it's requested`() = runTest {
        everySuspending { microphonePermission.request() } returns Unit
        every { microphonePermission.shouldShowInformationDialog() } returns false
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly

        screenViewModel.requireMicrophonePermission { }

        coVerify { microphonePermission.request() }
    }

    @Test
    fun `when the microphone permission is required abd given the function is executed`() =
        runTest {
            var wasCalled = false
            every { microphonePermission.granted } returns MutableStateFlow(true).readOnly

            screenViewModel.requireMicrophonePermission {
                wasCalled = true
            }

            coVerify { repeat(0) { microphonePermission.request() } }
            assertTrue(wasCalled)
        }

    @Test
    fun `when microphone permission dialog is shown onBackPressClick doesn't close screen`() =
        runTest {
            every { microphonePermission.granted } returns MutableStateFlow(false).readOnly
            every { microphonePermission.shouldShowInformationDialog() } returns true

            screenViewModel.requireMicrophonePermission { }

            assertEquals(
                MicrophonePermissionInfo,
                screenViewModel.screenViewState.value.dialogState
            )

            screenViewModel.onBackPressedClick()

            assertNull(screenViewModel.screenViewState.value.dialogState)
            coVerify { repeat(0) { navigator.onBackPressed() } }
        }

    @Test
    fun `when the overlay permission is required but not given it's requested`() = runTest {
        every { overlayPermission.request() } returns true
        every { overlayPermission.granted } returns MutableStateFlow(false).readOnly

        screenViewModel.requireOverlayPermission { }
        screenViewModel.onEvent(Confirm(OverlayPermissionInfo))

        coVerify { overlayPermission.request() }
    }

    @Test
    fun `when the overlay permission is required abd given the function is executed`() = runTest {
        var wasCalled = false
        every { overlayPermission.granted } returns MutableStateFlow(true).readOnly

        screenViewModel.requireOverlayPermission {
            wasCalled = true
        }

        coVerify { repeat(0) { overlayPermission.request() } }
        assertTrue(wasCalled)
    }

    @Test
    fun `when overlay permission dialog is shown onBackPressClick doesn't close screen`() =
        runTest {
            every { navigator.onBackPressed() } returns Unit
            every { overlayPermission.granted } returns MutableStateFlow(false).readOnly
            screenViewModel.requireOverlayPermission { }

            assertEquals(OverlayPermissionInfo, screenViewModel.screenViewState.value.dialogState)

            screenViewModel.onBackPressedClick()

            assertNull(screenViewModel.screenViewState.value.dialogState)
            coVerify { repeat(0) { navigator.onBackPressed() } }
        }

}