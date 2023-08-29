package org.rhasspy.mobile.data.log

import co.touchlab.kermit.Logger

enum class LogType {

    DialogManagerService,
    HomeAssistanceService,
    HttpClientService,
    IndicationService,
    LocalAudioService,
    MqttService,
    AudioPlayingService,
    IntentHandlingService,
    IntentRecognitionService,
    SpeechToTextService,
    TextToSpeechService,
    AppSettingsService,
    WakeWordService,
    WebServerService,
    VoiceActivityDetectionService;

    fun logger(): Logger {
        return Logger.withTag(this.name)
    }

}