package org.rhasspy.mobile.logic

import org.koin.dsl.module
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.services.audiofocus.AudioFocusService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingServiceParamsCreator
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

    single { AudioPlayingServiceParamsCreator() }
    single { AudioPlayingService(paramsCreator = get()) }

    single { DialogManagerServiceParamsCreator() }
    single { DialogManagerService(paramsCreator = get()) }

    single { HomeAssistantServiceParamsCreator() }
    single { HomeAssistantService(paramsCreator = get()) }

    single { HttpClientServiceParamsCreator() }
    single { HttpClientService(paramsCreator = get()) }

    single { IndicationService() }

    single { IntentHandlingServiceParamsCreator() }
    single { IntentHandlingService(paramsCreator = get()) }

    single { IntentRecognitionServiceParamsCreator() }
    single { IntentRecognitionService(paramsCreator = get()) }

    single { LocalAudioServiceParamsCreator() }
    single { LocalAudioService(paramsCreator = get()) }

    single { RecordingService(audioRecorder = get()) }

    single { AppSettingsService() }

    single { MqttServiceParamsCreator() }
    single { MqttService(paramsCreator = get()) }

    single { SpeechToTextServiceParamsCreator() }
    single { SpeechToTextService(paramsCreator = get()) }

    single { TextToSpeechServiceParamsCreator() }
    single { TextToSpeechService(paramsCreator = get()) }

    single { WakeWordServiceParamsCreator() }
    single { WakeWordService(paramsCreator = get()) }

    single { WebServerServiceParamsCreator() }
    single { WebServerService(paramsCreator = get()) }

    factory { params -> UdpConnection(params[0], params[1]) }

    single {
        FileLogger(
            nativeApplication = get()
        )
    }
}