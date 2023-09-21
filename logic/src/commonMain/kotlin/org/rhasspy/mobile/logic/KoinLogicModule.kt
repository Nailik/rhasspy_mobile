package org.rhasspy.mobile.logic

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.rhasspy.mobile.logic.connections.homeassistant.HomeAssistantConnection
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionParamsCreator
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.Rhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.IRhasspy3WyomingConnection
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.Rhasspy3WyomingConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnection
import org.rhasspy.mobile.logic.domains.asr.AsrDomain
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.handle.HandleDomain
import org.rhasspy.mobile.logic.domains.handle.IHandleDomain
import org.rhasspy.mobile.logic.domains.intent.IIntentDomain
import org.rhasspy.mobile.logic.domains.intent.IntentDomain
import org.rhasspy.mobile.logic.domains.mic.IMicDomain
import org.rhasspy.mobile.logic.domains.mic.MicDomain
import org.rhasspy.mobile.logic.domains.snd.ISndDomain
import org.rhasspy.mobile.logic.domains.snd.SndDomain
import org.rhasspy.mobile.logic.domains.tts.ITtsDomain
import org.rhasspy.mobile.logic.domains.tts.TtsDomain
import org.rhasspy.mobile.logic.domains.vad.IVadDomain
import org.rhasspy.mobile.logic.domains.vad.VadDomain
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.logic.domains.wake.UdpConnection
import org.rhasspy.mobile.logic.domains.wake.WakeDomain
import org.rhasspy.mobile.logic.local.audiofocus.AudioFocus
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.file.FilesStorage
import org.rhasspy.mobile.logic.local.file.IFileStorage
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.local.indication.Indication
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.local.localaudio.LocalAudioPlayer
import org.rhasspy.mobile.logic.local.settings.AppSettingsUtil
import org.rhasspy.mobile.logic.local.settings.IAppSettingsUtil
import org.rhasspy.mobile.logic.logger.DatabaseLogger
import org.rhasspy.mobile.logic.logger.IDatabaseLogger
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.pipeline.IPipeline
import org.rhasspy.mobile.logic.pipeline.Pipeline
import org.rhasspy.mobile.logic.pipeline.manager.PipelineManagerDisabled
import org.rhasspy.mobile.logic.pipeline.manager.PipelineManagerLocal
import org.rhasspy.mobile.logic.pipeline.manager.PipelineManagerMqtt

fun logicModule() = module {
    singleOf(::PipelineManagerLocal)
    singleOf(::PipelineManagerDisabled)
    singleOf(::PipelineManagerMqtt)

    single<IPipeline> {
        Pipeline(
            dispatcherProvider = get()
        )
    }

    single<IMicDomain> {
        MicDomain(
            pipeline = get(),
            audioRecorder = get(),
            microphonePermission = get(),
        )
    }


    single<IServiceMiddleware> {
        ServiceMiddleware(
        )
    }

    single<IAudioFocus> { AudioFocus() }

    single<ISndDomain> {
        SndDomain(
            pipeline = get(),
            audioFocusService = get(),
            localAudioService = get(),
            mqttClientService = get(),
            httpClientConnection = get(),
        )
    }

    single<IHomeAssistantConnection> { HomeAssistantConnection() }

    single<IRhasspy2HermesConnection> { Rhasspy2HermesConnection() }
    single<IRhasspy3WyomingConnection> { Rhasspy3WyomingConnection() }

    single<IIndication> {
        Indication()
    }

    single<IHandleDomain> {
        HandleDomain(
        pipeline= get(),
        homeAssistantConnection = get(),
        httpClientConnection = get(),
        )
    }

    single<IIntentDomain> {
        IntentDomain(
            pipeline = get(),
            mqttClientConnection = get(),
            httpClientConnection = get(),
            ) }

    single<ILocalAudioPlayer> {
        LocalAudioPlayer(
            nativeApplication = get(),
            audioFocusService = get(),
            ) }

    single<IAppSettingsUtil> { AppSettingsUtil() }

    singleOf(::MqttConnectionParamsCreator)
    single<IMqttConnection> {
        MqttConnection(
        pipeline = get(),
        appSettingsUtil = get(),
        paramsCreator = get()
        )
    }

    single<IAsrDomain> {
        AsrDomain(
            pipeline = get(),
            fileStorage = get(),
            mqttConnection = get(),
            rhasspy2HermesConnection = get(),
        )
    }

    single<ITtsDomain> {
        TtsDomain(
            pipeline = get(),
            mqttConnection = get(),
            rhasspy2HermesConnection = get(),
        )
    }

    single<IWakeDomain> {
        WakeDomain(
            pipeline = get(),
        )
    }

    single<IWebServerConnection> {
        WebServerConnection(
            pipeline = get(),
            appSettingsUtil = get(),
            fileStorage = get(),
            mqttConnection = get(),
            serviceMiddleware = get(),
        )
    }

    factory { params -> UdpConnection(params[0], params[1]) }

    single<IDatabaseLogger> {
        DatabaseLogger(
            nativeApplication = get(),
            externalResultRequest = get(),
            driverFactory = get(),
        )
    }

    single<IVadDomain> {
        VadDomain(
            pipeline = get(),
        )
    }

    single<IFileStorage> {
        FilesStorage(
            nativeApplication = get()
        )
    }
}