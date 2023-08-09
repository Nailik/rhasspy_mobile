package org.rhasspy.mobile.logic

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.logic.logger.IFileLogger
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.services.audiofocus.AudioFocusService
import org.rhasspy.mobile.logic.services.audiofocus.IAudioFocusService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingServiceParamsCreator
import org.rhasspy.mobile.logic.services.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParamsCreator
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.disabled.DialogManagerDisabled
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.local.*
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.mqtt.DialogManagerMqtt
import org.rhasspy.mobile.logic.services.dialog.states.IStateTransition
import org.rhasspy.mobile.logic.services.dialog.states.StateTransition
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantServiceParamsCreator
import org.rhasspy.mobile.logic.services.homeassistant.IHomeAssistantService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParamsCreator
import org.rhasspy.mobile.logic.services.httpclient.IHttpClientService
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.logic.services.indication.IndicationService
import org.rhasspy.mobile.logic.services.intenthandling.IIntentHandlingService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingServiceParamsCreator
import org.rhasspy.mobile.logic.services.intentrecognition.IIntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionServiceParamsCreator
import org.rhasspy.mobile.logic.services.localaudio.ILocalAudioService
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioServiceParamsCreator
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParamsCreator
import org.rhasspy.mobile.logic.services.settings.AppSettingsService
import org.rhasspy.mobile.logic.services.settings.IAppSettingsService
import org.rhasspy.mobile.logic.services.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextServiceParamsCreator
import org.rhasspy.mobile.logic.services.texttospeech.ITextToSpeechService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechServiceParamsCreator
import org.rhasspy.mobile.logic.services.wakeword.IWakeWordService
import org.rhasspy.mobile.logic.services.wakeword.UdpConnection
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordServiceParamsCreator
import org.rhasspy.mobile.logic.services.webserver.IWebServerService
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.logic.services.webserver.WebServerServiceParamsCreator

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

    factoryOf(::HomeAssistantServiceParamsCreator)
    single<IHomeAssistantService> { HomeAssistantService(paramsCreator = get()) }

    factoryOf(::HttpClientServiceParamsCreator)
    single<IHttpClientService> { HttpClientService(paramsCreator = get()) }

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

    factoryOf(::MqttServiceParamsCreator)
    single<IMqttService> { MqttService(paramsCreator = get()) }

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
            microphonePermission = get()
        )
    }

    factoryOf(::WebServerServiceParamsCreator)
    single<IWebServerService> { WebServerService(paramsCreator = get()) }

    factory { params -> UdpConnection(params[0], params[1]) }

    single<IFileLogger> {
        FileLogger(
            nativeApplication = get(),
            externalResultRequest = get()
        )
    }
}