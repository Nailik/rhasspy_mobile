package org.rhasspy.mobile

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import dev.icerock.moko.resources.desc.StringDesc
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.rhasspy.mobile.koin.factoryModule
import org.rhasspy.mobile.koin.nativeModule
import org.rhasspy.mobile.koin.serviceModule
import org.rhasspy.mobile.koin.viewModelModule
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.nativeutils.BackgroundService
import org.rhasspy.mobile.nativeutils.NativeApplication
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.services.dialog.DialogManagerService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.webserver.WebServerService
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.settings.types.LanguageType
import org.rhasspy.mobile.viewmodel.*
import org.rhasspy.mobile.viewmodel.configuration.*
import org.rhasspy.mobile.viewmodel.configuration.test.*
import org.rhasspy.mobile.viewmodel.screens.*
import org.rhasspy.mobile.viewmodel.settings.*

@Suppress("LeakingThis")
abstract class Application : NativeApplication(), KoinComponent {
    private val logger = Logger.withTag("Application")
    private val _isHasStarted = MutableStateFlow(false)
    val isHasStarted = _isHasStarted.readOnly

    init {
        println("init Application init")
        nativeInstance = this
        instance = this
    }

    companion object {
        lateinit var nativeInstance: NativeApplication
            private set
        lateinit var instance: Application
            private set
    }

    @OptIn(ExperimentalKermitApi::class)
    fun onCreated() {
        startKoin {
            // declare used modules
            modules(serviceModule, viewModelModule, factoryModule, nativeModule)
        }

        CoroutineScope(Dispatchers.Default).launch {
            if (!isDebug() && !isInstrumentedTest()) {
                Logger.addLogWriter(
                    CrashlyticsLogWriter(
                        minSeverity = Severity.Info,
                        minCrashSeverity = Severity.Assert
                    )
                )
            }
            Logger.addLogWriter(FileLogger)

            logger.i { "######## Application started ########" }


            setCrashlyticsCollectionEnabled(
                if (!isDebug() && !isInstrumentedTest()) {
                    AppSetting.isCrashlyticsEnabled.value
                } else false
            )

            //initialize/load the settings, generate the MutableStateFlow
            AppSetting
            ConfigurationSetting

            //setup language
            StringDesc.localeType = StringDesc.LocaleType.Custom(AppSetting.languageType.value.code)

            //start foreground service if enabled
            if (AppSetting.isBackgroundServiceEnabled.value) {
                BackgroundService.start()
            }

            //check if overlay permission is granted
            checkOverlayPermission()
            startServices()
            startOverlay()
            _isHasStarted.value = true
        }
    }

    fun setupLanguage() {
        val language: LanguageType = getSystemAppLanguage() ?: AppSetting.languageType.value
        StringDesc.localeType = StringDesc.LocaleType.Custom(language.code)
        AppSetting.languageType.value = language
        if(getDeviceLanguage() != language && getSystemAppLanguage() != language) {
            //only needs to be set if it differs from current settings and from device settings
            setLanguage(language)
        }
    }

    fun changeLanguage(language: LanguageType) {
        StringDesc.localeType = StringDesc.LocaleType.Custom(language.code)
        AppSetting.languageType.value = language
        if(getDeviceLanguage() != language && getSystemAppLanguage() != language) {
            //only needs to be set if it differs from current settings and from device settings
            setLanguage(language)
        }
    }

    /**
     * get the language of the device
     */
    abstract fun getDeviceLanguage(): LanguageType

    /**
     * get if the system has a custom language for this app
     */
    abstract fun getSystemAppLanguage(): LanguageType?

    /**
     * tell the system that this app should have a custom language
     */
    protected abstract fun setLanguage(languageType: LanguageType)

    override suspend fun updateWidgetNative() {
        updateWidget()
    }

    abstract fun startOverlay()

    abstract fun stopOverlay()

    abstract fun isDebug(): Boolean

    abstract fun isInstrumentedTest(): Boolean

    abstract suspend fun updateWidget()

    abstract fun setCrashlyticsCollectionEnabled(enabled: Boolean)

    abstract fun restart()

    fun startTest() {
        BackgroundService.stop()
        stopOverlay()
        unloadKoinModules(listOf(viewModelModule, serviceModule))
        loadKoinModules(listOf(serviceModule, viewModelModule))
    }

    fun stopTest() {
        reloadServiceModules()
    }

    fun reloadServiceModules() {
        stopOverlay()
        unloadKoinModules(listOf(viewModelModule, serviceModule))
        loadKoinModules(listOf(serviceModule, viewModelModule))
        startServices()
        startOverlay()
    }

    private fun checkOverlayPermission() {
        if (!OverlayPermission.isGranted()) {
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
        get<WebServerService>()
        get<MqttService>()
        get<DialogManagerService>()
    }

}