package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.background.BackgroundService
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention.OpenBatteryOptimizationSettings
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.Action.DisableBatteryOptimization
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BackgroundServiceSettingsViewModelTest : AppTest() {

    @RelaxedMockK
    lateinit var backgroundService: BackgroundService

    @RelaxedMockK
    lateinit var externalResultRequest: ExternalResultRequest

    private lateinit var backgroundServiceSettingsViewModel: BackgroundServiceSettingsViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { backgroundService }
                single { externalResultRequest }
            }
        )

        backgroundServiceSettingsViewModel = get()
    }

    @Test
    fun `when user enables background service setting the background service is started`() {
        backgroundServiceSettingsViewModel.onEvent(BackgroundServiceSettingsUiEvent.Change.SetBackgroundServiceSettingsEnabled(true))

        assertEquals(true, AppSetting.isBackgroundServiceEnabled.value)
        verify { backgroundService.start() }
    }

    @Test
    fun `when user disables background service setting the background service is stopped`() {
        backgroundServiceSettingsViewModel.onEvent(BackgroundServiceSettingsUiEvent.Change.SetBackgroundServiceSettingsEnabled(false))

        assertEquals(false, AppSetting.isBackgroundServiceEnabled.value)
        verify { backgroundService.stop() }
    }

    @Test
    fun `when user wants to disable battery optimization he is redirected to app settings when the permission is not given afterwards a snack bar is shown`() {
        backgroundServiceSettingsViewModel.onEvent(DisableBatteryOptimization)

        verify { externalResultRequest.launch(OpenBatteryOptimizationSettings) }
    }

}