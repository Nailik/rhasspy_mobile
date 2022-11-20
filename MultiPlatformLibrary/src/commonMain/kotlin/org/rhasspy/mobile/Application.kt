package org.rhasspy.mobile

import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.rhasspy.mobile.interfaces.HomeAssistantInterface
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.mqtt.OverlayServices
import org.rhasspy.mobile.services.LocalAudioService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService
import org.rhasspy.mobile.services.statemachine.StateMachineService
import org.rhasspy.mobile.services.webserver.WebServerService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

enum class ServiceTestName {
    StateMachine,
    StateMachineTest,
    RhasspyActions,
    RhasspyActionsTest,
    Mqtt,
    MqttTest,
    HttpClient,
    HttpClientTest,
    WebServer,
    WebServerTest;
}

val serviceModule = module {
    single { WebServerService() }
    single { HttpClientService() }
    single { LocalAudioService() }
    single { HomeAssistantInterface }

    single(named(ServiceTestName.StateMachine)) { StateMachineService() }
    single(named(ServiceTestName.RhasspyActions)) { RhasspyActionsService() }
    single(named(ServiceTestName.Mqtt)) { MqttService() }
    single(named(ServiceTestName.HttpClient)) { HttpClientService() }
    single(named(ServiceTestName.WebServer)) { WebServerService() }

    single(named(ServiceTestName.StateMachineTest)) { StateMachineService(isTest = true) }
    factory(named(ServiceTestName.RhasspyActionsTest)) { params -> RhasspyActionsService(params.get(), isTest = true) }
    factory(named(ServiceTestName.MqttTest)) { params -> MqttService(params.get(), isTest = true) }
    factory(named(ServiceTestName.HttpClientTest)) { params -> HttpClientService(params.get(), isTest = true) }
    factory(named(ServiceTestName.WebServerTest)) { params -> WebServerService(params.get(), isTest = true) }

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
        MqttService

        StringDesc.localeType = StringDesc.LocaleType.Custom(AppSettings.languageOption.value.code)

        //start all services
        CoroutineScope(Dispatchers.Default).launch {
            //ServiceInterface.serviceAction(ServiceAction.Start)
            get<WebServerService>().start()
        }

    }

}