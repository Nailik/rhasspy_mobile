package org.rhasspy.mobile.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Action.OverlayPermissionDialogResult
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import kotlin.test.*

class KViewModelTest : KoinTest {

    @RelaxedMockK
    lateinit var navigator: Navigator

    @RelaxedMockK
    lateinit var nativeApplication: NativeApplication

    @RelaxedMockK
    lateinit var microphonePermission: MicrophonePermission

    @RelaxedMockK
    lateinit var overlayPermission: OverlayPermission

    @RelaxedMockK
    lateinit var function: () -> Unit

    private lateinit var kViewModel: KViewModel

    @BeforeTest
    fun before() {
        startKoin {
            modules(
                module {
                    single { nativeApplication }
                    single { navigator }
                    single { microphonePermission }
                    single { overlayPermission }
                }
            )
        }

        MockKAnnotations.init(this, relaxUnitFun = false)
        kViewModel = object : KViewModel() {}
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun `when a view model is composed it informs the navigator`() {
        kViewModel.composed()
        verify { navigator.onComposed(kViewModel) }
    }

    @Test
    fun `when a view model is disposed it informs the navigator`() {
        kViewModel.disposed()
        verify { navigator.onDisposed(kViewModel) }
    }

    @Test
    fun `when the microphone permission is required but not given it's requested`() {
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly

        kViewModel.requireMicrophonePermission(function)

        coVerify { microphonePermission.request() }
    }

    @Test
    fun `when the microphone permission is required abd given the function is executed`() {
        every { microphonePermission.granted } returns MutableStateFlow(true).readOnly

        kViewModel.requireMicrophonePermission(function)

        coVerify(exactly = 0) { microphonePermission.request() }
        verify { function() }
    }

    @Test
    fun `when microphone permission dialog is shown onBackPressClick doesn't close screen`() {
        every { microphonePermission.granted } returns MutableStateFlow(false).readOnly
        every { microphonePermission.shouldShowInformationDialog() } returns true

        kViewModel.requireMicrophonePermission(function)

        assertTrue { kViewModel.kViewState.value.isShowMicrophonePermissionInformationDialog }

        kViewModel.onBackPressedClick()

        assertFalse { kViewModel.kViewState.value.isShowMicrophonePermissionInformationDialog }
        coVerify(exactly = 0) { navigator.onBackPressed() }
    }

    @Test
    fun `when the overlay permission is required but not given it's requested`() {
        every { overlayPermission.granted } returns MutableStateFlow(false).readOnly

        kViewModel.requireOverlayPermission(Unit, function)
        kViewModel.onEvent(OverlayPermissionDialogResult(true))

        coVerify { overlayPermission.request() }
    }

    @Test
    fun `when the overlay permission is required abd given the function is executed`() {
        every { overlayPermission.granted } returns MutableStateFlow(true).readOnly

        kViewModel.requireOverlayPermission(Unit, function)

        coVerify(exactly = 0) { overlayPermission.request() }
        verify { function() }
    }

    @Test
    fun `when overlay permission dialog is shown onBackPressClick doesn't close screen`() {
        every { overlayPermission.granted } returns MutableStateFlow(false).readOnly
        kViewModel.requireOverlayPermission(Unit, function)

        assertTrue { kViewModel.kViewState.value.isShowOverlayPermissionInformationDialog }

        kViewModel.onBackPressedClick()

        assertFalse { kViewModel.kViewState.value.isShowOverlayPermissionInformationDialog }
        coVerify(exactly = 0) { navigator.onBackPressed() }
    }

}