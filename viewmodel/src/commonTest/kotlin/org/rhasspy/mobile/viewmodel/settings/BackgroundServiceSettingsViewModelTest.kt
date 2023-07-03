package org.rhasspy.mobile.viewmodel.settings

import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.background.IBackgroundService
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention.OpenBatteryOptimizationSettings
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.nVerify
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.Action.DisableBatteryOptimization
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BackgroundServiceSettingsViewModelTest : AppTest() {

    @Mock
    lateinit var backgroundService: IBackgroundService

    @Mock
    lateinit var externalResultRequest: IExternalResultRequest

    private lateinit var backgroundServiceSettingsViewModel: BackgroundServiceSettingsViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { backgroundService } //TODO check if overriding
                single { externalResultRequest }
            }
        )

        backgroundServiceSettingsViewModel = get()
    }

    @Test
    fun `when user enables background service setting the background service is started`() {
        backgroundServiceSettingsViewModel.onEvent(BackgroundServiceSettingsUiEvent.Change.SetBackgroundServiceSettingsEnabled(true))

        assertEquals(true, AppSetting.isBackgroundServiceEnabled.value)
        nVerify { backgroundService.start() }
    }

    @Test
    fun `when user disables background service setting the background service is stopped`() {
        backgroundServiceSettingsViewModel.onEvent(BackgroundServiceSettingsUiEvent.Change.SetBackgroundServiceSettingsEnabled(false))

        assertEquals(false, AppSetting.isBackgroundServiceEnabled.value)
        nVerify { backgroundService.stop() }
    }

    @Test
    fun `when user wants to disable battery optimization he is redirected to app settings when the permission is not given afterwards a snack bar is shown`() {
        backgroundServiceSettingsViewModel.onEvent(DisableBatteryOptimization)

        nVerify { externalResultRequest.launch(OpenBatteryOptimizationSettings) }
    }

}