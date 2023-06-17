package org.rhasspy.mobile.viewmodel.settings

import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption.Disabled
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.KViewModelUiEvent.Action.OverlayPermissionDialogResult
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SelectMicrophoneOverlaySizeOption
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MicrophoneOverlaySettingsViewModelTest : AppTest() {

    @RelaxedMockK
    lateinit var overlayPermission: OverlayPermission

    private lateinit var microphoneOverlaySettingsViewModel: MicrophoneOverlaySettingsViewModel

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
        verify(exactly = 0) { overlayPermission.request() }
        assertEquals(Disabled, AppSetting.microphoneOverlaySizeOption.value)
    }

    @Test
    fun `when the user select another overlay option than disabled and doesn't grant overlay permission the option is not saved`() {
        AppSetting.microphoneOverlaySizeOption.value = Disabled
        every { overlayPermission.granted } returns MutableStateFlow(false)
        every { overlayPermission.request() } returns false

        MicrophoneOverlaySizeOption.values().filter { it != Disabled }.forEach { option ->

            microphoneOverlaySettingsViewModel.onEvent(SelectMicrophoneOverlaySizeOption(option))
            microphoneOverlaySettingsViewModel.onEvent(OverlayPermissionDialogResult(true))

            verify { overlayPermission.request() }
            assertEquals(Disabled, AppSetting.microphoneOverlaySizeOption.value)
        }
    }

    @Test
    fun `when the user select a overlay option and overlay permission is already granted the option is saved`() {
        AppSetting.microphoneOverlaySizeOption.value = Disabled
        every { overlayPermission.granted } returns MutableStateFlow(true)

        MicrophoneOverlaySizeOption.values().forEach { option ->

            microphoneOverlaySettingsViewModel.onEvent(SelectMicrophoneOverlaySizeOption(option))

            verify(exactly = 0) { overlayPermission.request() }
            assertEquals(option, AppSetting.microphoneOverlaySizeOption.value)
        }
    }

}