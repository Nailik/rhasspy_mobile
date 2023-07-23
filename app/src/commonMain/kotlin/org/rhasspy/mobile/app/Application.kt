package org.rhasspy.mobile.app

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.logic.logger.IFileLogger
import org.rhasspy.mobile.logic.logicModule
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.logic.services.webserver.IWebServerService
import org.rhasspy.mobile.overlay.IIndicationOverlay
import org.rhasspy.mobile.overlay.IMicrophoneOverlay
import org.rhasspy.mobile.overlay.koinOverlayModule
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.background.IBackgroundService
import org.rhasspy.mobile.platformspecific.firebase.ICrashlytics
import org.rhasspy.mobile.platformspecific.language.ILanguageUtils
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.platformspecific.platformSpecificModule
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.settingsModule
import org.rhasspy.mobile.viewmodel.viewModelModule

class Application : NativeApplication(), KoinComponent {

    private val logger = Logger.withTag("Application")
    private val _isHasStarted = MutableStateFlow(false)
    override val isHasStarted = _isHasStarted.readOnly

    @OptIn(ExperimentalKermitApi::class)
    override fun onCreated() {
        startKoin {
            // declare used modules
            modules(
                koinApplicationModule,
                logicModule(),
                viewModelModule(),
                koinOverlayModule(),
                settingsModule,
                platformSpecificModule
            )
        }

        CoroutineScope(get<IDispatcherProvider>().IO).launch {

            Logger.addLogWriter(get<IFileLogger>() as LogWriter)
            if (!isDebug() && !isInstrumentedTest()) {
                Logger.addLogWriter(
                    CrashlyticsLogWriter(
                        minSeverity = Severity.Info,
                        minCrashSeverity = Severity.Assert
                    )
                )
            }

            logger.i { "######## Application \n started ########" }

            //initialize/load the settings, generate the MutableStateFlow
            AppSetting
            ConfigurationSetting

            get<ICrashlytics>().setEnabled(
                if (!isDebug() && !isInstrumentedTest()) {
                    AppSetting.isCrashlyticsEnabled.value
                } else false
            )

            //setup language
            AppSetting.languageType.value = get<ILanguageUtils>().setupLanguage(AppSetting.languageType.value)
            StringDesc.localeType = StringDesc.LocaleType.Custom(AppSetting.languageType.value.code)

            //start foreground service if enabled
            if (AppSetting.isBackgroundServiceEnabled.value) {
                get<IBackgroundService>().start()
            }

            //check if overlay permission is granted
            resume()
            _isHasStarted.value = true
        }
    }

    private fun startOverlay() {
        CoroutineScope(get<IDispatcherProvider>().Main).launch {
            get<IIndicationOverlay>().start()
            get<IMicrophoneOverlay>().start()
        }
    }

    @Suppress("unused")
    fun stopOverlay() {
        CoroutineScope(get<IDispatcherProvider>().Main).launch {
            get<IIndicationOverlay>().stop()
            get<IMicrophoneOverlay>().stop()
        }
    }

    override fun resume() {
        get<IMicrophonePermission>().update()
        get<IOverlayPermission>().update()
        checkOverlayPermission()
        startServices()
        startOverlay()
    }

    private fun checkOverlayPermission() {
        if (!get<IOverlayPermission>().isGranted()) {
            if (AppSetting.microphoneOverlaySizeOption.value != MicrophoneOverlaySizeOption.Disabled ||
                AppSetting.isWakeWordLightIndicationEnabled.value
            ) {
                logger.w { "reset overlay settings because permission is missing" }
                //reset services that need the permission
                AppSetting.microphoneOverlaySizeOption.value = MicrophoneOverlaySizeOption.Disabled
                AppSetting.isWakeWordLightIndicationEnabled.value = false
            }
        }
    }

    private fun startServices() {
        get<IWebServerService>()
        get<IMqttService>()
        get<IDialogManagerService>().start()
    }

}