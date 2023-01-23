package org.rhasspy.mobile.logic.logger

import co.touchlab.kermit.Logger

enum class LogType {

    DialogManagerService,
    HomeAssistanceService,
    HttpClientService,
    IndicationService,
    LocalAudioService,
    MqttService,
    RecordingService,
    AudioPlayingService,
    IntentHandlingService,
    IntentRecognitionService,
    SpeechToTextService,
    TextToSpeechService,
    AppSettingsService,
    UdpService,
    WakeWordService,
    WebServerService;

    fun logger(): Logger {
        return Logger.withTag(this.name)
    }

}