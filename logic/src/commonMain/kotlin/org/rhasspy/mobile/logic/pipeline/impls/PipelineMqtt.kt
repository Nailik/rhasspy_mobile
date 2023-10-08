package org.rhasspy.mobile.logic.pipeline.impls

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.*
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrError
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrTextCaptured
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentNotRecognized
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentRecognitionResult
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult.PlayBytes
import org.rhasspy.mobile.logic.connections.mqtt.toAudio
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.pipeline.*
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.IntentResult.Intent
import org.rhasspy.mobile.logic.pipeline.PipelineResult.End
import org.rhasspy.mobile.logic.pipeline.Source.Rhasspy2HermesMqtt
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.Transcript
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class PipelineMqtt(
    private val mqttConnection: IMqttConnection,
    private val domains: DomainBundle,
    private val domainHistory: IDomainHistory,
    private val audioFocus: IAudioFocus,
) : IPipeline {

    private val logger = Logger.withTag("PipelineMqtt")

    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun runPipeline(wakeResult: WakeResult): PipelineResult {

        logger.d { "runPipeline $wakeResult" }

        scope.launch {
            mqttConnection.incomingMessages
                .filterIsInstance<PlayBytes>()
                .collect {
                    domains.sndDomain.awaitPlayAudio(it.id, it.toAudio())
                }
        }

        return runPipeline(wakeResult.sessionId).also {
            domains.dispose()
            scope.cancel()
        }
    }

    private suspend fun runPipeline(newSessionId: String?): PipelineResult {
        logger.a { "rrunPipeline23" }

        var sessionId: String? = newSessionId

        return mqttConnection.incomingMessages
            .filterIsInstance<SessionEvent>()
            .onEach {
                if (sessionId == null) {
                    logger.a { "setSessionId to ${it.sessionId}" }
                    if (it is SessionStarted) { //TODO #466 timeout?
                        onSessionStarted(it.sessionId ?: return@onEach)
                        sessionId = it.sessionId
                    }
                    Unit
                }
            }
            .filter { it.sessionId == sessionId }
            .map { event ->
                sessionId?.let { id ->
                    when (event) {
                        is AsrError                -> Unit
                        is AsrTextCaptured         -> onAsrTextCaptured(id, event.text ?: "")
                        is EndSession              -> End(Rhasspy2HermesMqtt)
                        is IntentNotRecognized     -> Unit
                        is IntentRecognitionResult -> onIntentRecognitionResult(id, event.intentName, event.intent)
                        is Say                     -> onSay(id, event.text, event.volume)
                        is SessionEnded            -> End(Rhasspy2HermesMqtt)
                        is StartListening          -> onStartListening(id)
                        is SessionStarted          -> Unit
                        is StartSession            -> Unit
                        is StopListening           -> Unit
                    }
                } ?: Unit
            }
            .filterIsInstance<End>()
            .first()

    }

    private fun onSessionStarted(sessionId: String) {
        domainHistory.addToHistory(
            sessionId = sessionId,
            PipelineStarted(
                sessionId = sessionId,
                source = Rhasspy2HermesMqtt,
            )
        )
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
                domains.sndDomain.awaitPlayAudio(sessionId, result)
            }
        }
    }

    private fun onStartListening(sessionId: String) {
        scope.launch {
            domains.asrDomain.awaitTranscript(
                sessionId = sessionId,
                audioStream = domains.micDomain.audioStream,
                awaitVoiceStart = domains.vadDomain::awaitVoiceStart, //TODO this fucks everything up because start listening is send to
                awaitVoiceStopped = domains.vadDomain::awaitVoiceStopped,
            ).also {
                audioFocus.abandon(AudioFocusRequestReason.Record)
            }
        }
    }

}