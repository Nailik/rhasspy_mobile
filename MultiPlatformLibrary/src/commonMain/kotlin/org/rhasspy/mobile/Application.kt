package org.rhasspy.mobile

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import dev.icerock.moko.resources.desc.StringDesc
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceTestMiddleware
import org.rhasspy.mobile.mqtt.OverlayServices
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.services.dialog.DialogManagerService
import org.rhasspy.mobile.services.dialog.DialogManagerServiceParams
import org.rhasspy.mobile.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.services.homeassistant.HomeAssistantServiceParams
import org.rhasspy.mobile.services.wakeword.WakeWordService
import org.rhasspy.mobile.services.wakeword.WakeWordServiceParams
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.services.indication.IndicationService
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.services.localaudio.LocalAudioServiceParams
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams
import org.rhasspy.mobile.services.settings.AppSettingsService
import org.rhasspy.mobile.services.udp.UdpService
import org.rhasspy.mobile.services.udp.UdpServiceParams
import org.rhasspy.mobile.services.webserver.WebServerService
import org.rhasspy.mobile.services.webserver.WebServerServiceParams
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewmodel.*
import org.rhasspy.mobile.viewmodel.configuration.*
import org.rhasspy.mobile.viewmodel.configuration.test.*
import org.rhasspy.mobile.viewmodel.overlay.IndicationOverlayViewModel
import org.rhasspy.mobile.viewmodel.overlay.MicrophoneOverlayViewModel
import org.rhasspy.mobile.viewmodel.screens.*
import org.rhasspy.mobile.viewmodel.settings.*
import org.rhasspy.mobile.viewmodel.settings.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.sound.WakeIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.widget.MicrophoneWidgetViewModel

inline fun <reified T : Closeable> Module.closeableSingle(
    qualifier: Qualifier? = null,
    createdAtStart: Boolean = false,
    noinline definition: Definition<T>
) {
    single(qualifier, createdAtStart, definition) onClose {
        it?.close()
    }
}

val serviceModule = module {
    closeableSingle { LocalAudioService() }
    closeableSingle { RhasspyActionsService() }
    closeableSingle { MqttService() }
    closeableSingle { HttpClientService() }
    closeableSingle { WebServerService() }
    closeableSingle { UdpService() }
    closeableSingle { HomeAssistantService() }
    closeableSingle { RecordingService() }
    closeableSingle { WakeWordService() }
    closeableSingle { DialogManagerService() }
    closeableSingle { AppSettingsService() }
    closeableSingle { IndicationService() }

    closeableSingle { params -> createServiceMiddleware(params.getOrNull() ?: false) }

    single { params -> params.getOrNull<LocalAudioServiceParams>() ?: LocalAudioServiceParams() }
    single { params ->
        params.getOrNull<RhasspyActionsServiceParams>() ?: RhasspyActionsServiceParams()
    }
    single { params -> params.getOrNull<MqttServiceParams>() ?: MqttServiceParams() }
    single { params -> params.getOrNull<HttpClientServiceParams>() ?: HttpClientServiceParams() }
    single { params -> params.getOrNull<WebServerServiceParams>() ?: WebServerServiceParams() }
    single { params -> params.getOrNull<UdpServiceParams>() ?: UdpServiceParams() }
    single { params ->
        params.getOrNull<HomeAssistantServiceParams>() ?: HomeAssistantServiceParams()
    }
    single { params -> params.getOrNull<WakeWordServiceParams>() ?: WakeWordServiceParams() }
    single { params ->
        params.getOrNull<DialogManagerServiceParams>() ?: DialogManagerServiceParams()
    }

    closeableSingle { AudioPlayingConfigurationTest() }
    closeableSingle { DialogManagementConfigurationTest() }
    closeableSingle { IntentHandlingConfigurationTest() }
    closeableSingle { IntentRecognitionConfigurationTest() }
    closeableSingle { MqttConfigurationTest() }
    closeableSingle { RemoteHermesHttpConfigurationTest() }
    closeableSingle { SpeechToTextConfigurationTest() }
    closeableSingle { TextToSpeechConfigurationTest() }
    closeableSingle { WakeWordConfigurationTest() }
    closeableSingle { WebServerConfigurationTest() }
}

val viewModelModule = module {
    single { AppViewModel() }
    single { HomeScreenViewModel() }
    single { MicrophoneWidgetViewModel() }
    single { ConfigurationScreenViewModel() }
    single { AudioPlayingConfigurationViewModel() }
    single { DialogManagementConfigurationViewModel() }
    single { IntentHandlingConfigurationViewModel() }
    single { IntentRecognitionConfigurationViewModel() }
    single { MqttConfigurationViewModel() }
    single { RemoteHermesHttpConfigurationViewModel() }
    single { SpeechToTextConfigurationViewModel() }
    single { TextToSpeechConfigurationViewModel() }
    single { WakeWordConfigurationViewModel() }
    single { WebServerConfigurationViewModel() }
    single { LogScreenViewModel() }
    single { SettingsScreenViewModel() }
    single { AboutScreenViewModel() }
    single { AutomaticSilenceDetectionSettingsViewModel() }
    single { BackgroundServiceSettingsViewModel() }
    single { DeviceSettingsSettingsViewModel() }
    single { IndicationSettingsViewModel() }
    single { WakeIndicationSoundSettingsViewModel() }
    single { RecordedIndicationSoundSettingsViewModel() }
    single { ErrorIndicationSoundSettingsViewModel() }
    single { LanguageSettingsViewModel() }
    single { LogSettingsViewModel() }
    single { MicrophoneOverlaySettingsViewModel() }
    single { SaveAndRestoreSettingsViewModel() }
    single { MicrophoneOverlayViewModel() }
    single { IndicationOverlayViewModel() }
}

val nativeModule = module {
    closeableSingle { AudioRecorder() }
}


fun createServiceMiddleware(isTest: Boolean): IServiceMiddleware {
    return when (isTest) {
        true -> ServiceTestMiddleware()
        false -> ServiceMiddleware()
    }
}

abstract class Application : NativeApplication(), KoinComponent {
    private val logger = Logger.withTag("Application")

    companion object {
        fun startServices() {

        }

        fun reloadServiceModules() {
            unloadKoinModules(serviceModule)
            loadKoinModules(serviceModule)
        }

        lateinit var Instance: NativeApplication
            private set
    }

    init {
        @Suppress("LeakingThis")
        Instance = this
    }

    @OptIn(ExperimentalKermitApi::class)
    fun onCreated() {
        // start a KoinApplication in Global context
        startKoin {
            // declare used modules
            modules(serviceModule, viewModelModule, nativeModule)
        }

        Logger.addLogWriter(CrashlyticsLogWriter())
        Logger.addLogWriter(FileLogger)

        CoroutineScope(Dispatchers.Default).launch {
            AppSettings.isCrashlyticsEnabled.data.collect {
                setCrashlyticsCollectionEnabled(it)
            }
        }

        logger.a { "######## Application started ########" }

        //initialize/load the settings, generate the MutableStateFlow
        AppSettings
        ConfigurationSettings
        OverlayServices.checkPermission()
        startNativeServices()

        StringDesc.localeType = StringDesc.LocaleType.Custom(AppSettings.languageOption.value.code)
    }

    abstract fun setCrashlyticsCollectionEnabled(enabled: Boolean)

    override suspend fun updateWidgetNative() {
        updateWidget()
    }

    abstract suspend fun updateWidget()

}