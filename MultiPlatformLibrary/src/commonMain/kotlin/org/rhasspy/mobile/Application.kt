package org.rhasspy.mobile

import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.desc.StringDesc
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.rhasspy.mobile.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.mqtt.OverlayServices
import org.rhasspy.mobile.services.LocalAudioService
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.services.homeassistant.HomeAssistantServiceParams
import org.rhasspy.mobile.services.httpclient.HttpClientParams
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams
import org.rhasspy.mobile.services.statemachine.StateMachineService
import org.rhasspy.mobile.services.statemachine.StateMachineServiceParams
import org.rhasspy.mobile.services.udp.UdpService
import org.rhasspy.mobile.services.udp.UdpServiceParams
import org.rhasspy.mobile.services.webserver.WebServerService
import org.rhasspy.mobile.services.webserver.WebServerServiceParams
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

val serviceModule = module {
    single { LocalAudioService() }

    single { StateMachineService() }
    single { RhasspyActionsService() }
    single { MqttService() }
    single { HttpClientService() }
    single { WebServerService() }
    single { UdpService() }
    single { HomeAssistantService() }

    single { params -> params.getOrNull<StateMachineServiceParams>() ?: StateMachineServiceParams() }
    single { params -> params.getOrNull<RhasspyActionsServiceParams>() ?: RhasspyActionsServiceParams() }
    single { params -> params.getOrNull<MqttServiceParams>() ?: MqttServiceParams() }
    single { params -> params.getOrNull<HttpClientParams>() ?: HttpClientParams() }
    single { params -> params.getOrNull<WebServerServiceParams>() ?: WebServerServiceParams() }
    single { params -> params.getOrNull<UdpServiceParams>() ?: UdpServiceParams() }
    single { params -> params.getOrNull<HomeAssistantServiceParams>() ?: HomeAssistantServiceParams() }
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
        ServiceInterface

        StringDesc.localeType = StringDesc.LocaleType.Custom(AppSettings.languageOption.value.code)
    }

}