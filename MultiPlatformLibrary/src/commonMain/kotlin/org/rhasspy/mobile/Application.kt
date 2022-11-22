package org.rhasspy.mobile

import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.desc.StringDesc
import io.ktor.utils.io.core.*
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.rhasspy.mobile.logger.EventLogger
import org.rhasspy.mobile.logger.EventTag
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.mqtt.OverlayServices
import org.rhasspy.mobile.services.ServiceWatchdog
import org.rhasspy.mobile.services.dialogManager.DialogManagerServiceParams
import org.rhasspy.mobile.services.dialogManager.IDialogManagerService
import org.rhasspy.mobile.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.services.homeassistant.HomeAssistantServiceParams
import org.rhasspy.mobile.services.hotword.HotWordService
import org.rhasspy.mobile.services.hotword.HotWordServiceParams
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.indication.IndicationService
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams
import org.rhasspy.mobile.services.settings.AppSettingsService
import org.rhasspy.mobile.services.statemachine.StateMachineService
import org.rhasspy.mobile.services.statemachine.StateMachineServiceParams
import org.rhasspy.mobile.services.udp.UdpService
import org.rhasspy.mobile.services.udp.UdpServiceParams
import org.rhasspy.mobile.services.webserver.WebServerService
import org.rhasspy.mobile.services.webserver.WebServerServiceParams
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

inline fun <reified T : Closeable> Module.closeableSingle(
    qualifier: Qualifier? = null,
    createdAtStart: Boolean = false,
    noinline definition: Definition<T>
) {
    single(qualifier, createdAtStart, definition) onClose { it?.close() }
}

val serviceModule = module {
    closeableSingle { LocalAudioService() }
    closeableSingle { StateMachineService() }
    closeableSingle { RhasspyActionsService() }
    closeableSingle { MqttService() }
    closeableSingle { HttpClientService() }
    closeableSingle { WebServerService() }
    closeableSingle { UdpService() }
    closeableSingle { HomeAssistantService() }
    closeableSingle { RecordingService() }
    closeableSingle { HotWordService() }
    closeableSingle { ServiceWatchdog() }
    closeableSingle { IDialogManagerService.getService() }
    closeableSingle { AppSettingsService() }
    closeableSingle { IndicationService }

    single { params -> params.getOrNull<StateMachineServiceParams>() ?: StateMachineServiceParams() }
    single { params -> params.getOrNull<RhasspyActionsServiceParams>() ?: RhasspyActionsServiceParams() }
    single { params -> params.getOrNull<MqttServiceParams>() ?: MqttServiceParams() }
    single { params -> params.getOrNull<HttpClientServiceParams>() ?: HttpClientServiceParams() }
    single { params -> params.getOrNull<WebServerServiceParams>() ?: WebServerServiceParams() }
    single { params -> params.getOrNull<UdpServiceParams>() ?: UdpServiceParams() }
    single { params -> params.getOrNull<HomeAssistantServiceParams>() ?: HomeAssistantServiceParams() }
    single { params -> params.getOrNull<HotWordServiceParams>() ?: HotWordServiceParams() }
    single { params -> params.getOrNull<DialogManagerServiceParams>() ?: DialogManagerServiceParams() }

    EventTag.values().forEach { eventTag ->
        println("created single for ${eventTag.name}")
        single(named(eventTag.name)) { EventLogger(eventTag) }
    }
}

abstract class Application : NativeApplication(), KoinComponent {
    private val logger = Logger.withTag("Application")

    companion object {
        lateinit var Instance: NativeApplication
            private set
    }

    init {
        @Suppress("LeakingThis")
        Instance = this
    }

    fun onCreated() {
        // start a KoinApplication in Global context
        startKoin {
            // declare used modules
            modules(serviceModule)
        }

        Logger.addLogWriter(FileLogger)

        logger.a { "######## Application started ########" }

        //initialize/load the settings, generate the MutableStateFlow
        AppSettings
        ConfigurationSettings
        OverlayServices.checkPermission()
        startNativeServices()
        //makes sure that the MutableStateFlow inside those objects are created in ui thread because they internally use livedata which cannot be
        // created in background tread

        StringDesc.localeType = StringDesc.LocaleType.Custom(AppSettings.languageOption.value.code)
    }

}