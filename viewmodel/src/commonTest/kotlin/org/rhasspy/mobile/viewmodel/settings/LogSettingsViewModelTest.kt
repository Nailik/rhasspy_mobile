package org.rhasspy.mobile.viewmodel.settings

import co.touchlab.kermit.Logger
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.nVerify
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Change.SetCrashlyticsEnabled
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Change.SetLogLevel
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LogSettingsViewModelTest : AppTest() {

    @Mock
    lateinit var nativeApplication: INativeApplication

    private lateinit var logSettingsViewModel: LogSettingsViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { nativeApplication }
            }
        )

        logSettingsViewModel = get()
    }

    @Test
    fun `when user select crashlytics to be enabled or disabled it is saved and set in the application`() {

        logSettingsViewModel.onEvent(SetCrashlyticsEnabled(true))

        nVerify { nativeApplication.setCrashlyticsCollectionEnabled(true) }
        assertEquals(true, AppSetting.isCrashlyticsEnabled.value)


        logSettingsViewModel.onEvent(SetCrashlyticsEnabled(false))

        nVerify { nativeApplication.setCrashlyticsCollectionEnabled(false) }
        assertEquals(false, AppSetting.isCrashlyticsEnabled.value)

    }

    @Test
    fun `when user selects log level it is saved and the logger is updated`() {

        LogLevel.values().forEach { logLevel ->

            logSettingsViewModel.onEvent(SetLogLevel(logLevel))
            assertEquals(logLevel, AppSetting.logLevel.value)
            assertEquals(logLevel.severity, Logger.config.minSeverity)

        }

    }

}