package org.rhasspy.mobile.logic

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.rhasspy.mobile.logic.connections.homeassistant.HomeAssistantConnection
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.Rhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.IRhasspy3WyomingConnection
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.Rhasspy3WyomingConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnection
import org.rhasspy.mobile.logic.dialog.DialogManagerService
import org.rhasspy.mobile.logic.dialog.DialogManagerServiceParamsCreator
import org.rhasspy.mobile.logic.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.dialog.dialogmanager.disabled.DialogManagerDisabled
import org.rhasspy.mobile.logic.dialog.dialogmanager.local.*
import org.rhasspy.mobile.logic.dialog.dialogmanager.mqtt.DialogManagerMqtt
import org.rhasspy.mobile.logic.dialog.states.IStateTransition
import org.rhasspy.mobile.logic.dialog.states.StateTransition
import org.rhasspy.mobile.logic.domains.asr.AsrDomain
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.dialog.dialogmanager.local.*
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

fun logicModule() = module {
    single<IPipeline> {
        Pipeline()
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
            dialogManagerService = get(),
            speechToTextService = get(),
            textToSpeechService = get(),
            appSettingsService = get(),
            sndDomain = get(),
            mqttService = get(),
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

    factory { DialogManagerServiceParamsCreator() }
    single<IDialogManagerService> {
        DialogManagerService(
            dispatcherProvider = get(),
            mqttService = get()
        )
    }

    single<IStateTransition> {
        StateTransition(
            paramsCreator = get(),
            dispatcherProvider = get(),
            dialogManagerService = get(),
            audioFocusService = get(),
            wakeWordService = get(),
            intentRecognitionService = get(),
            speechToTextService = get(),
            indicationService = get(),
            mqttService = get(),
        )
    }

    single<ISessionStateActions> {
        SessionStateActions(
            dialogManagerService = get(),
            indicationService = get(),
            stateTransition = get(),
            speechToTextService = get(),
            intentHandlingService = get(),

            )
    }

    single<IIdleStateActions> {
        IdleStateActions(
            dialogManagerService = get(),
            stateTransition = get(),
            wakeWordService = get(),
            indicationService = get(),
            audioPlayingService = get(),
        )
    }

    single<IPlayingAudioStateActions> {
        PlayingAudioStateActions(
            dialogManagerService = get(),
            audioPlayingService = get(),
            stateTransition = get(),
        )
    }

    singleOf(::DialogManagerLocal)
    singleOf(::DialogManagerMqtt)
    singleOf(::DialogManagerDisabled)

    single<IHomeAssistantConnection> { HomeAssistantConnection() }

    single<IRhasspy2HermesConnection> { Rhasspy2HermesConnection() }
    single<IRhasspy3WyomingConnection> { Rhasspy3WyomingConnection() }

    single<IIndication> {
        Indication()
    }

    single<IHandleDomain> { HandleDomain(paramsCreator = get()) }

    single<IIntentDomain> { IntentDomain(paramsCreator = get()) }

    single<ILocalAudioPlayer> { LocalAudioPlayer(paramsCreator = get()) }

    single<IAppSettingsUtil> { AppSettingsUtil() }

    single<IMqttConnection> { MqttConnection(paramsCreator = get()) }

    single<IAsrDomain> {
        AsrDomain(
            paramsCreator = get(),
            audioRecorder = get()
        )
    }

    single<ITtsDomain> { TtsDomain(paramsCreator = get()) }

    single<IWakeDomain> {
        WakeDomain(
            paramsCreator = get(),
            audioRecorder = get(),
        )
    }

    single<IWebServerConnection> { WebServerConnection() }

    factory { params -> UdpConnection(params[0], params[1]) }

    single<IDatabaseLogger> {
        DatabaseLogger(
            nativeApplication = get(),
            externalResultRequest = get(),
            driverFactory = get()
        )
    }

    single<IVadDomain> { params ->
        VadDomain(
            paramsCreator = get(),
            audioRecorder = params[0]
        )
    }
}