package org.rhasspy.mobile.viewmodel.settings

import kotlinx.coroutines.flow.MutableStateFlow
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption.Disabled
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.nVerify
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Dialog.Confirm
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.OverlayPermissionInfo
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SelectMicrophoneOverlaySizeOption
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MicrophoneOverlaySettingsViewModelTest : AppTest() {

    @Mock
    lateinit var overlayPermission: IOverlayPermission

    private lateinit var microphoneOverlaySettingsViewModel: MicrophoneOverlaySettingsViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { overlayPermission }
            }
        )

        microphoneOverlaySettingsViewModel = get()
    }

    @Test
    fun `when the user select disabled overlay option overlay permission is not required`() {
        AppSetting.microphoneOverlaySizeOption.value = Disabled
        every { overlayPermission.granted } returns MutableStateFlow(false)

        microphoneOverlaySettingsViewModel.onEvent(SelectMicrophoneOverlaySizeOption(Disabled))
        nVerify { repeat(0) { overlayPermission.request() } }
        assertEquals(Disabled, AppSetting.microphoneOverlaySizeOption.value)
    }

    @Test
    fun `when the user select another overlay option than disabled and doesn't grant overlay permission the option is not saved`() {
        AppSetting.microphoneOverlaySizeOption.value = Disabled
        every { overlayPermission.granted } returns MutableStateFlow(false)
        every { overlayPermission.request() } returns false

        MicrophoneOverlaySizeOption.values().filter { it != Disabled }.forEach { option ->

            microphoneOverlaySettingsViewModel.onEvent(SelectMicrophoneOverlaySizeOption(option))
            microphoneOverlaySettingsViewModel.onEvent(Confirm(OverlayPermissionInfo))

            nVerify { overlayPermission.request() }
            assertEquals(Disabled, AppSetting.microphoneOverlaySizeOption.value)
        }

        assertTrue { true }
    }

    @Test
    fun `when the user select a overlay option and overlay permission is already granted the option is saved`() {
        AppSetting.microphoneOverlaySizeOption.value = Disabled
        every { overlayPermission.granted } returns MutableStateFlow(true)

        MicrophoneOverlaySizeOption.values().forEach { option ->

            microphoneOverlaySettingsViewModel.onEvent(SelectMicrophoneOverlaySizeOption(option))

            nVerify { repeat(0) { overlayPermission.request() } }
            assertEquals(option, AppSetting.microphoneOverlaySizeOption.value)
        }
    }

}