package org.rhasspy.mobile.logic.pipeline.impls

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.pipeline.*
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.IntentResult.Intent
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.Transcript
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class PipelineLocal(
    private val domains: DomainBundle,
    private val domainHistory: IDomainHistory,
    private val audioFocus: IAudioFocus,
) : IPipeline, KoinComponent {
    //TODO #466 move audio hints to here

    private val logger = Logger.withTag("PipelineDisabled")
    override suspend fun runPipeline(wakeResult: WakeResult): PipelineResult {

        logger.d { "runPipeline $wakeResult" }

        //use session id from event or create own one
        val sessionId = wakeResult.sessionId ?: uuid4().toString()

        domainHistory.addToHistory(
            sessionId = sessionId,
            PipelineStarted(
                sessionId = sessionId,
                source = Source.Local,
            )
        )

        //transcript audio to text from voice start till voice stop
        val transcript = when (
            val result = domains.asrDomain.awaitTranscript(
                startRecording = StartRecording(
                    sessionId = sessionId,
                    source = Source.Local,
                ),
                audioStream = domains.micDomain.audioStream,
                awaitVoiceStart = domains.vadDomain::awaitVoiceStart,
                awaitVoiceStopped = domains.vadDomain::awaitVoiceStopped,
            ).also {
                audioFocus.abandon(Record)
            }
        ) {
            is Transcript     -> result
            is PipelineResult -> return result
        }

        //find intent from text, eventually already handles
        val intent = domains.intentDomain.awaitIntent(
            sessionId = sessionId,
            transcript = transcript
        )

        //handle intent
        val handle = when (intent) {
            is Handle         -> intent
            is Intent         -> {
                when (
                    val result = domains.handleDomain.awaitIntentHandle(
                        sessionId = sessionId,
                        intent = intent
                    )
                ) {
                    is Handle         -> result
                    is PipelineResult -> return result
                }
            }

            is PipelineResult -> return intent
        }

        //translate handle text to speech
        val tts = when (val result = domains.ttsDomain.onSynthesize(
            sessionId = sessionId,
            volume = AppSetting.volume.value,
            siteId = ConfigurationSetting.siteId.value,
            handle = handle,
        )) {
            is Audio          -> result
            is PipelineResult -> return result
        }

        //play audio
        return domains.sndDomain.awaitPlayAudio(sessionId, tts).also {
            domains.dispose()
        }
    }

}