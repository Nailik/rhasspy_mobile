package org.rhasspy.mobile.app

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.domains.handle.IHandleDomain
import org.rhasspy.mobile.logic.domains.intent.IIntentDomain
import org.rhasspy.mobile.logic.logger.IDatabaseLogger
import org.rhasspy.mobile.logic.logicModule
import org.rhasspy.mobile.logic.pipeline.IPipelineManager
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
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.migrations.SettingsInitializer
import org.rhasspy.mobile.settings.settingsModule
import org.rhasspy.mobile.viewmodel.viewModelModule

class Application : NativeApplication(), KoinComponent {

    private val logger = Logger.withTag("Application")
    private val _isHasStarted = MutableStateFlow(false)
    override val isHasStarted = _isHasStarted.readOnly

    @OptIn(ExperimentalKermitApi::class)
    override fun onCreated() {

        Logger.setMinSeverity(Severity.Verbose)
        startKoin {
            // declare used modules
            modules(
                koinApplicationModule,
                logicModule(),
                viewModelModule(),
                koinOverlayModule(),
                settingsModule(),
                platformSpecificModule
            )
        }

        CoroutineScope(get<IDispatcherProvider>().IO).launch {
            Logger.addLogWriter(get<IDatabaseLogger>() as LogWriter)
            if (!isInstrumentedTest()) {
                Logger.addLogWriter(
                    CrashlyticsLogWriter(
                        minSeverity = Severity.Info,
                        minCrashSeverity = Severity.Assert
                    )
                )
            }
            logger.i { "######## Application \n started ########" }

            //initialize/load the settings, generate the MutableStateFlow, migrate settings if necessary
            SettingsInitializer.initialize()
            Logger.setMinSeverity(AppSetting.logLevel.value.severity)
            AppSetting
            ConfigurationSetting

            get<ICrashlytics>().setEnabled(
                if (!isInstrumentedTest()) {
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
            _isHasStarted.value = true
            resume()
        }
    }

    override suspend fun resume() {
        _isHasStarted.first { it } //await for app start
        get<IMicrophonePermission>().update()
        get<IOverlayPermission>().update()
        //start services
        get<IWebServerConnection>()
        get<IMqttConnection>()
        get<IPipelineManager>()
        get<IIntentDomain>()
        get<IHandleDomain>()
        //start overlay
        checkOverlayPermission()
        get<IIndicationOverlay>().start()
        get<IMicrophoneOverlay>().start()

        if (AppSetting.isBackgroundServiceEnabled.value) {
            get<IBackgroundService>().start()
        }
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

}