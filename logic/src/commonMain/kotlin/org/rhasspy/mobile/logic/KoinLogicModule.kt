package org.rhasspy.mobile.logic

import org.koin.core.module.dsl.factoryOf
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
import org.rhasspy.mobile.logic.domains.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.domains.audioplaying.AudioPlayingServiceParamsCreator
import org.rhasspy.mobile.logic.domains.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.domains.dialog.DialogManagerService
import org.rhasspy.mobile.logic.domains.dialog.DialogManagerServiceParamsCreator
import org.rhasspy.mobile.logic.domains.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.domains.dialog.dialogmanager.disabled.DialogManagerDisabled
import org.rhasspy.mobile.logic.domains.dialog.dialogmanager.local.*
import org.rhasspy.mobile.logic.domains.dialog.dialogmanager.mqtt.DialogManagerMqtt
import org.rhasspy.mobile.logic.domains.dialog.states.IStateTransition
import org.rhasspy.mobile.logic.domains.dialog.states.StateTransition
import org.rhasspy.mobile.logic.domains.intenthandling.IIntentHandlingService
import org.rhasspy.mobile.logic.domains.intenthandling.IntentHandlingService
import org.rhasspy.mobile.logic.domains.intenthandling.IntentHandlingServiceParamsCreator
import org.rhasspy.mobile.logic.domains.intentrecognition.IIntentRecognitionService
import org.rhasspy.mobile.logic.domains.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.domains.intentrecognition.IntentRecognitionServiceParamsCreator
import org.rhasspy.mobile.logic.domains.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.domains.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.domains.speechtotext.SpeechToTextServiceParamsCreator
import org.rhasspy.mobile.logic.domains.texttospeech.ITextToSpeechService
import org.rhasspy.mobile.logic.domains.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.domains.texttospeech.TextToSpeechServiceParamsCreator
import org.rhasspy.mobile.logic.domains.voiceactivitydetection.IVoiceActivityDetectionService
import org.rhasspy.mobile.logic.domains.voiceactivitydetection.VoiceActivityDetectionParamsCreator
import org.rhasspy.mobile.logic.domains.voiceactivitydetection.VoiceActivityDetectionService
import org.rhasspy.mobile.logic.domains.wakeword.IWakeWordService
import org.rhasspy.mobile.logic.domains.wakeword.UdpConnection
import org.rhasspy.mobile.logic.domains.wakeword.WakeWordService
import org.rhasspy.mobile.logic.domains.wakeword.WakeWordServiceParamsCreator
import org.rhasspy.mobile.logic.local.audiofocus.AudioFocusService
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocusService
import org.rhasspy.mobile.logic.local.indication.IIndicationService
import org.rhasspy.mobile.logic.local.indication.IndicationService
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioService
import org.rhasspy.mobile.logic.local.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.local.localaudio.LocalAudioServiceParamsCreator
import org.rhasspy.mobile.logic.local.settings.AppSettingsService
import org.rhasspy.mobile.logic.local.settings.IAppSettingsService
import org.rhasspy.mobile.logic.logger.DatabaseLogger
import org.rhasspy.mobile.logic.logger.IDatabaseLogger
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware

fun logicModule() = module {
    single<IServiceMiddleware> {
        ServiceMiddleware(
            dialogManagerService = get(),
            speechToTextService = get(),
            textToSpeechService = get(),
            appSettingsService = get(),
            localAudioService = get(),
            mqttService = get(),
        )
    }

    single<IAudioFocusService> { AudioFocusService() }

    factory { AudioPlayingServiceParamsCreator() }
    single<IAudioPlayingService> { AudioPlayingService(paramsCreator = get()) }

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

    single<IIndicationService> {
        IndicationService()
    }

    factoryOf(::IntentHandlingServiceParamsCreator)
    single<IIntentHandlingService> { IntentHandlingService(paramsCreator = get()) }

    factoryOf(::IntentRecognitionServiceParamsCreator)
    single<IIntentRecognitionService> { IntentRecognitionService(paramsCreator = get()) }

    factoryOf(::LocalAudioServiceParamsCreator)
    single<ILocalAudioService> { LocalAudioService(paramsCreator = get()) }

    single<IAppSettingsService> { AppSettingsService() }

    factoryOf(::MqttConnectionParamsCreator)
    single<IMqttConnection> { MqttConnection(paramsCreator = get()) }

    factoryOf(::SpeechToTextServiceParamsCreator)
    single<ISpeechToTextService> {
        SpeechToTextService(
            paramsCreator = get(),
            audioRecorder = get()
        )
    }

    factoryOf(::TextToSpeechServiceParamsCreator)
    single<ITextToSpeechService> { TextToSpeechService(paramsCreator = get()) }

    factoryOf(::WakeWordServiceParamsCreator)
    single<IWakeWordService> {
        WakeWordService(
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

    factoryOf(::VoiceActivityDetectionParamsCreator)
    single<IVoiceActivityDetectionService> { params ->
        VoiceActivityDetectionService(
            paramsCreator = get(),
            audioRecorder = params[0]
        )
    }
}