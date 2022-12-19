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
import org.rhasspy.mobile.services.dialogManager.DialogManagerServiceParams
import org.rhasspy.mobile.services.dialogManager.IDialogManagerService
import org.rhasspy.mobile.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.services.homeassistant.HomeAssistantServiceParams
import org.rhasspy.mobile.services.hotword.HotWordService
import org.rhasspy.mobile.services.hotword.HotWordServiceParams
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
import org.rhasspy.mobile.viewModels.HomeScreenViewModel
import org.rhasspy.mobile.viewModels.MicrophoneWidgetViewModel
import org.rhasspy.mobile.viewModels.configuration.test.*


inline fun <reified T : Closeable> Module.closeableSingle(
    qualifier: Qualifier? = null,
    createdAtStart: Boolean = false,
    noinline definition: Definition<T>
) {
    single(qualifier, createdAtStart, definition) onClose {
        // println("onCLose $definition $it")
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
    closeableSingle { HotWordService() }
    closeableSingle { IDialogManagerService.getService() }
    closeableSingle { AppSettingsService() }
    closeableSingle { IndicationService }

    closeableSingle { params -> createServiceMiddleware(params.getOrNull() ?: false) }

    single { params -> params.getOrNull<LocalAudioServiceParams>() ?: LocalAudioServiceParams() }
    single { params -> params.getOrNull<RhasspyActionsServiceParams>() ?: RhasspyActionsServiceParams() }
    single { params -> params.getOrNull<MqttServiceParams>() ?: MqttServiceParams() }
    single { params -> params.getOrNull<HttpClientServiceParams>() ?: HttpClientServiceParams() }
    single { params -> params.getOrNull<WebServerServiceParams>() ?: WebServerServiceParams() }
    single { params -> params.getOrNull<UdpServiceParams>() ?: UdpServiceParams() }
    single { params -> params.getOrNull<HomeAssistantServiceParams>() ?: HomeAssistantServiceParams() }
    single { params -> params.getOrNull<HotWordServiceParams>() ?: HotWordServiceParams() }
    single { params -> params.getOrNull<DialogManagerServiceParams>() ?: DialogManagerServiceParams() }

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

    single { HomeScreenViewModel() }
    single { MicrophoneWidgetViewModel() }
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
            modules(serviceModule, viewModelModule)
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

    abstract val viewModelModule: Module

    abstract fun setCrashlyticsCollectionEnabled(enabled: Boolean)

    override suspend fun updateWidgetNative() {
        updateWidget()
    }

    abstract suspend fun updateWidget()

}