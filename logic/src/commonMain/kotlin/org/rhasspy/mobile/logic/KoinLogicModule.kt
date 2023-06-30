package org.rhasspy.mobile.logic

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.services.audiofocus.AudioFocusService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingServiceParamsCreator
import org.rhasspy.mobile.logic.services.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParamsCreator
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantServiceParamsCreator
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParamsCreator
import org.rhasspy.mobile.logic.services.indication.IndicationService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingServiceParamsCreator
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionServiceParamsCreator
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioServiceParamsCreator
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParamsCreator
import org.rhasspy.mobile.logic.services.recording.RecordingService
import org.rhasspy.mobile.logic.services.settings.AppSettingsService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextServiceParamsCreator
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechServiceParamsCreator
import org.rhasspy.mobile.logic.services.wakeword.UdpConnection
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordServiceParamsCreator
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.logic.services.webserver.WebServerServiceParamsCreator

val logicModule = module {
    single {
        ServiceMiddleware(
            dialogManagerService = get(),
            speechToTextService = get(),
            textToSpeechService = get(),
            appSettingsService = get(),
            localAudioService = get(),
            mqttService = get(),
            wakeWordService = get()
        )
    }

    single { AudioFocusService() }

    factoryOf(::AudioPlayingServiceParamsCreator)
    singleOf(::AudioPlayingService)

    factoryOf(::DialogManagerServiceParamsCreator)
    singleOf(::DialogManagerService)

    factoryOf(::HomeAssistantServiceParamsCreator)
    singleOf(::HomeAssistantService)

    factoryOf(::HttpClientServiceParamsCreator)
    singleOf(::HttpClientService)

    singleOf(::IndicationService)

    factoryOf(::IntentHandlingServiceParamsCreator)
    singleOf(::IntentHandlingService)

    factoryOf(::IntentRecognitionServiceParamsCreator)
    singleOf(::IntentRecognitionService)

    factoryOf(::LocalAudioServiceParamsCreator)
    singleOf(::LocalAudioService)

    singleOf(::RecordingService)

    singleOf(::AppSettingsService)

    factoryOf(::MqttServiceParamsCreator)
    singleOf(::MqttService)

    factoryOf(::SpeechToTextServiceParamsCreator)
    singleOf(::SpeechToTextService)

    factoryOf(::TextToSpeechServiceParamsCreator)
    singleOf(::TextToSpeechService)

    factoryOf(::WakeWordServiceParamsCreator)
    singleOf(::WakeWordService)

    factoryOf(::WebServerServiceParamsCreator)
    singleOf(::WebServerService)

    factory { params -> UdpConnection(params[0], params[1]) }

    singleOf(::FileLogger)
}