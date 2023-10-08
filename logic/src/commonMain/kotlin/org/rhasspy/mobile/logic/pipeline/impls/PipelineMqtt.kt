package org.rhasspy.mobile.logic.pipeline.impls

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.*
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrError
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrTextCaptured
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentNotRecognized
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentRecognitionResult
import org.rhasspy.mobile.logic.connections.mqtt.toAudio
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.pipeline.*
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.IntentResult.Intent
import org.rhasspy.mobile.logic.pipeline.IntentResult.IntentError
import org.rhasspy.mobile.logic.pipeline.PipelineResult.End
import org.rhasspy.mobile.logic.pipeline.Source.Rhasspy2HermesMqtt
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.Transcript
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.TranscriptError
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class PipelineMqtt(
    private val mqttConnection: IMqttConnection,
    private val domains: DomainBundle,
    private val audioFocus: IAudioFocus,
) : IPipeline {

    private val logger = Logger.withTag("PipelineMqtt")

    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun runPipeline(wakeResult: WakeResult): PipelineResult {

        logger.d { "runPipeline $wakeResult" }

        scope.launch {
            mqttConnection.incomingMessages
                .filterIsInstance<PlayResult.PlayBytes>()
                .collect {
                    domains.sndDomain.awaitPlayAudio(it.toAudio())
                }
        }

        if (wakeResult.sessionId != null) return runPipeline(wakeResult.sessionId)

        mqttConnection.hotWordDetected(wakeResult.name ?: "Unknown")

        //use session id from event or wait for session to start
        val sessionId = mqttConnection.incomingMessages
            .filterIsInstance<SessionStarted>()
            .mapNotNull { it.sessionId }
            .first() //TODO #466 timeout?

        logger.d { "SessionStarted $sessionId" }

        return runPipeline(sessionId).also {
            domains.dispose()
            scope.cancel()
        }
    }

    private suspend fun runPipeline(sessionId: String): PipelineResult {

        return mqttConnection.incomingMessages
            .filterIsInstance<SessionEvent>()
            .filter { it.sessionId == sessionId }
            .map {
                when (it) {
                    is AsrError                -> TranscriptError(
                        reason = Reason.Error(MR.strings.asr_error.stable),
                        source = Rhasspy2HermesMqtt,
                    )

                    is AsrTextCaptured         -> onAsrTextCaptured(sessionId, it.text ?: "")
                    is EndSession              -> End(Rhasspy2HermesMqtt)
                    is IntentNotRecognized     -> IntentError(
                        reason = Reason.Error(MR.strings.intent_not_recognized.stable),
                        source = Rhasspy2HermesMqtt,
                    )
                    is IntentRecognitionResult -> onIntentRecognitionResult(sessionId, it.intentName, it.intent)
                    is Say                     -> onSay(sessionId, it.text, it.volume)
                    is SessionEnded            -> End(Rhasspy2HermesMqtt)
                    is StartListening          -> onStartListening(sessionId)
                    is SessionStarted          -> Unit
                    is StartSession            -> Unit
                    is StopListening           -> Unit
                }
            }
            .filterIsInstance<End>()
            .first()

    }

    private fun onAsrTextCaptured(sessionId: String, text: String) {
        scope.launch {
            //find intent from text, eventually already handles
            domains.intentDomain.awaitIntent(
                sessionId = sessionId,
                transcript = Transcript(
                    text = text,
                    source = Rhasspy2HermesMqtt
                ),
            )
        }
    }

    private fun onIntentRecognitionResult(sessionId: String, intentName: String?, intent: String) {
        scope.launch {
            domains.handleDomain.awaitIntentHandle(
                sessionId = sessionId,
                intent = Intent(
                    intentName = intentName,
                    intent = intent,
                    source = Rhasspy2HermesMqtt
                )
            )
        }
    }

    private fun onSay(sessionId: String, text: String, volume: Float?) {
        scope.launch {
            val result = domains.ttsDomain.onSynthesize(
                sessionId = sessionId,
                volume = AppSetting.volume.value,
                siteId = ConfigurationSetting.siteId.value,
                handle = Handle(
                    text = text,
                    volume = volume,
                    source = Rhasspy2HermesMqtt
                ),
            )

            if (result is Audio) {
                domains.sndDomain.awaitPlayAudio(result)
            }
        }
    }

    private fun onStartListening(sessionId: String) {
        scope.launch {
            domains.asrDomain.awaitTranscript(
                sessionId = sessionId,
                audioStream = domains.micDomain.audioStream,
                awaitVoiceStart = domains.vadDomain::awaitVoiceStart,
                awaitVoiceStopped = domains.vadDomain::awaitVoiceStopped,
            ).also {
                audioFocus.abandon(AudioFocusRequestReason.Record)
            }
        }
    }

}