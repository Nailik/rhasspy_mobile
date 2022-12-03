package org.rhasspy.mobile.middleware

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

sealed interface EventType {

    val title: StringResource

    enum class HotWordServiceEventType(override val title: StringResource) : EventType {
        InitializePorcupine(MR.strings.startPorcupine),
        StartPorcupine(MR.strings.startPorcupine),
        Detected(MR.strings.hotWordDetected);
    }

    enum class HttpClientServiceEventType(override val title: StringResource) : EventType {
        Start(MR.strings.start),
        SpeechToText(MR.strings.speechToText),
        RecognizeIntent(MR.strings.recognizeIntent),
        TextToSpeech(MR.strings.textToSpeech),
        PlayWav(MR.strings.playWav),
        IntentHandling(MR.strings.intentHandling),
        HassEvent(MR.strings.hassEvent),
        HassIntent(MR.strings.hassIntent);
    }

    enum class WebServerServiceEventType(override val title: StringResource) : EventType {
        Start(MR.strings.start),
        Received(MR.strings.received);
    }

    enum class UdpServiceEventType(override val title: StringResource) : EventType {
        Start(MR.strings.start),
        StreamAudio(MR.strings.streamAudio);
    }

    enum class MqttServiceEventType(override val title: StringResource) : EventType {
        Start(MR.strings.start),
        Connecting(MR.strings.connecting),
        Disconnect(MR.strings.disconnect),
        Reconnect(MR.strings.reconnect),
        SubscribeTopic(MR.strings.subscribeTopic),
        Subscribing(MR.strings.subscribing),
        Publish(MR.strings.publish),
        Received(MR.strings.received);
    }

    enum class RhasspyActionServiceEventType(override val title: StringResource) : EventType {
        RecognizeIntent(MR.strings.startPorcupine),
        Say(MR.strings.say),
        PlayAudio(MR.strings.playAudio),
        SpeechToText(MR.strings.speechToText),
        IntentHandling(MR.strings.intentHandling);
    }

    enum class HomeAssistantServiceEventType(override val title: StringResource) : EventType {
        SendIntent(MR.strings.sendIntent);
    }

    enum class IndicationServiceEventType(override val title: StringResource) : EventType {
        Start(MR.strings.start);
    }

    enum class RecordingServiceEventType(override val title: StringResource) : EventType {
        Start(MR.strings.start),
        Stop(MR.strings.stop);
    }

}

val EventType.name: StringResource
    get() = when (this) {
        is EventType.HomeAssistantServiceEventType -> MR.strings.homeAssistantService
        is EventType.HotWordServiceEventType -> MR.strings.hotWordService
        is EventType.HttpClientServiceEventType -> MR.strings.httpClientService
        is EventType.IndicationServiceEventType -> MR.strings.indicationService
        is EventType.MqttServiceEventType -> MR.strings.mqttService
        is EventType.RhasspyActionServiceEventType -> MR.strings.rhasspyActionService
        is EventType.UdpServiceEventType -> MR.strings.udpService
        is EventType.WebServerServiceEventType -> MR.strings.webServerService
        is EventType.RecordingServiceEventType -> MR.strings.recordingService
    }