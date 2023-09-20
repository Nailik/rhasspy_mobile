package org.rhasspy.mobile.logic.pipeline

import kotlinx.datetime.Instant
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

sealed interface PipelineEvent {

    sealed interface AudioDomainEvent : PipelineEvent {

        data class AudioStartEvent(
            val sessionId: String,
            val timeStamp: Instant,
            val sampleRate: AudioFormatSampleRateType,
            val encoding: AudioFormatEncodingType,
            val channel: AudioFormatChannelType,
        ) : AudioDomainEvent

        class AudioChunkEvent(
            val sessionId: String,
            val timeStamp: Instant,
            val sampleRate: AudioFormatSampleRateType,
            val encoding: AudioFormatEncodingType,
            val channel: AudioFormatChannelType,
            val data: ByteArray,
        ) : AudioDomainEvent

        data class AudioStopEvent(
            val sessionId: String,
            val timeStamp: Instant,
        ) : AudioDomainEvent

    }

    sealed interface WakeDomainEvent : PipelineEvent {

        data class DetectionEvent(
            val name: String,
            val timeStamp: Instant,
        ) : WakeDomainEvent

        data object NotDetectedEvent : WakeDomainEvent

    }

    sealed interface VadDomainEvent : PipelineEvent {

        data class VoiceStartedEvent(
            val timeStamp: Instant,
        ) : VadDomainEvent

        data class VoiceStoppedEvent(
            val timeStamp: Instant,
        ) : VadDomainEvent

        data object VadTimeoutEvent : VadDomainEvent

    }

    sealed interface AsrDomainEvent : PipelineEvent {

        data class TranscriptEvent(
            val text: String,
        ) : AsrDomainEvent

        data object TranscriptErrorEvent : AsrDomainEvent

        data object TranscriptTimeoutEvent : AsrDomainEvent

    }

    sealed interface IntentDomainEvent : PipelineEvent {

        data class RecognizeEvent(
            val text: String,
            val sessionId: String
        ) : IntentDomainEvent

        data class IntentEvent(
            val name: String?,
            val entities: String,
        ) : IntentDomainEvent

        data class NotRecognizedEvent(
            val text: String,
        ) : IntentDomainEvent

        data object IntentTimeoutEvent : IntentDomainEvent

    }

    sealed interface HandleDomainEvent : PipelineEvent {

        data class HandledEvent(
            val text: String,
        ) : HandleDomainEvent

        data class NotHandledEvent(
            val text: String,
        ) : HandleDomainEvent

    }

    sealed interface TtsDomainEvent : PipelineEvent {

        data class SynthesizeEvent(
            val text: String,
            val volume: Float?,
            val siteId: String,
            val sessionId: String
        ) : TtsDomainEvent

        data object TtsErrorEvent : TtsDomainEvent

    }

    sealed interface SndDomainEvent : PipelineEvent {

        data object PlayedEvent : SndDomainEvent

    }

}
