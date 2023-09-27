package org.rhasspy.mobile.logic.pipeline

import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.*
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrError
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrTextCaptured
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentNotRecognized
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentRecognitionResult
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.handle.IHandleDomain
import org.rhasspy.mobile.logic.domains.intent.IIntentDomain
import org.rhasspy.mobile.logic.domains.mic.IMicDomain
import org.rhasspy.mobile.logic.domains.tts.ITtsDomain
import org.rhasspy.mobile.logic.domains.vad.IVadDomain
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.IntentResult.Intent
import org.rhasspy.mobile.logic.pipeline.IntentResult.NotRecognized
import org.rhasspy.mobile.logic.pipeline.PipelineResult.End
import org.rhasspy.mobile.logic.pipeline.Source.Rhasspy2HermesMqtt
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.Transcript
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.TranscriptError
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

class PipelineMqtt(
    private val mqttConnection: IMqttConnection,
    private val intentDomain: IIntentDomain,
    private val handleDomain: IHandleDomain,
    private val ttsDomain: ITtsDomain,
    private val asrDomain: IAsrDomain,
    private val micDomain: IMicDomain,
    private val vadDomain: IVadDomain,
    private val audioFocus: IAudioFocus,
) : IPipeline {

    override suspend fun runPipeline(startEvent: StartEvent): PipelineResult {

        //use session id from event or wait for session to start
        val sessionId = startEvent.sessionId ?: mqttConnection.incomingMessages
            .filterIsInstance<SessionStarted>()
            .mapNotNull { it.sessionId }
            .first()

        return runPipeline(sessionId)
    }


    private suspend fun runPipeline(sessionId: String): PipelineResult {

        return mqttConnection.incomingMessages
            .filterIsInstance<SessionEvent>()
            .filter { it.sessionId == sessionId }
            .map {
                when (it) {
                    is AsrError                -> TranscriptError(Rhasspy2HermesMqtt)
                    is AsrTextCaptured         -> onAsrTextCaptured(sessionId, it.text ?: "")
                    is EndSession              -> End(Rhasspy2HermesMqtt)
                    is IntentNotRecognized     -> NotRecognized(Rhasspy2HermesMqtt)
                    is IntentRecognitionResult -> onIntentRecognitionResult(sessionId, it.intentName, it.intent)
                    is Say                     -> onSay(sessionId, it.text, it.volume)
                    is SessionEnded            -> End(Rhasspy2HermesMqtt)
                    is StartListening          -> onStartListening(sessionId)
                    is SessionStarted          -> runPipeline(sessionId)
                    is StartSession            -> runPipeline(sessionId)
                    is StopListening           -> runPipeline(sessionId)
                }
            }.first()
    }

    private suspend fun onAsrTextCaptured(sessionId: String, text: String): PipelineResult {
        //find intent from text, eventually already handles
        intentDomain.awaitIntent(
            sessionId = sessionId,
            transcript = Transcript(text, Rhasspy2HermesMqtt),
        )
        return runPipeline(sessionId)
    }

    private suspend fun onIntentRecognitionResult(sessionId: String, intentName: String?, intent: String): PipelineResult {
        handleDomain.awaitIntentHandle(
            sessionId = sessionId,
            intent = Intent(intentName, intent, Rhasspy2HermesMqtt)
        )
        return runPipeline(sessionId)
    }

    private suspend fun onSay(sessionId: String, text: String, volume: Float?): PipelineResult {
        ttsDomain.onSynthesize(
            sessionId = sessionId,
            volume = AppSetting.volume.value,
            siteId = ConfigurationSetting.siteId.value,
            handle = Handle(text = text, volume = volume, Rhasspy2HermesMqtt),
        )
        return runPipeline(sessionId)
    }

    private suspend fun onStartListening(sessionId: String): PipelineResult {
        asrDomain.awaitTranscript(
            sessionId = sessionId,
            audioStream = micDomain.audioStream,
            awaitVoiceStart = vadDomain::awaitVoiceStart,
            awaitVoiceStopped = vadDomain::awaitVoiceStopped,
        ).also {
            audioFocus.abandon(AudioFocusRequestReason.Record)
        }
        return runPipeline(sessionId)
    }

}