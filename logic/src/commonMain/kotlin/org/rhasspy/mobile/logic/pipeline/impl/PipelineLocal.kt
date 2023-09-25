package org.rhasspy.mobile.logic.pipeline.impl

import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.handle.IHandleDomain
import org.rhasspy.mobile.logic.domains.intent.IIntentDomain
import org.rhasspy.mobile.logic.domains.mic.IMicDomain
import org.rhasspy.mobile.logic.domains.snd.ISndDomain
import org.rhasspy.mobile.logic.domains.tts.ITtsDomain
import org.rhasspy.mobile.logic.domains.vad.IVadDomain
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.HandleResult.NotHandled
import org.rhasspy.mobile.logic.pipeline.IntentResult.Intent
import org.rhasspy.mobile.logic.pipeline.IntentResult.NotRecognized
import org.rhasspy.mobile.logic.pipeline.PipelineResult
import org.rhasspy.mobile.logic.pipeline.SndResult.Played
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.Transcript
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.TranscriptError
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.logic.pipeline.TtsResult.NotSynthesized
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

class PipelineLocal(
    private val asrDomain: IAsrDomain,
    private val handleDomain: IHandleDomain,
    private val intentDomain: IIntentDomain,
    private val micDomain: IMicDomain,
    private val sndDomain: ISndDomain,
    private val ttsDomain: ITtsDomain,
    private val vadDomain: IVadDomain,
    private val audioFocus: IAudioFocus,
) : IPipeline {

    override suspend fun runPipeline(sessionId: String): PipelineResult {
        //transcript audio to text from voice start till voice stop
        val transcript = when (
            val result = asrDomain.awaitTranscript(
                sessionId = sessionId,
                audioStream = micDomain.audioStream,
                awaitVoiceStart = vadDomain::awaitVoiceStart,
                awaitVoiceStopped = vadDomain::awaitVoiceStopped,
            ).also {
                audioFocus.abandon(Record)
            }
        ) {
            is Transcript      -> result
            is TranscriptError -> return result
        }

        //find intent from text, eventually already handles
        val intent = intentDomain.awaitIntent(
            sessionId = sessionId,
            transcript = transcript
        )

        //handle intent
        val handle = when (intent) {
            is Handle        -> intent
            is NotHandled    -> return intent
            is Intent        -> {
                when (
                    val result = handleDomain.awaitIntentHandle(
                        sessionId = sessionId,
                        intent = intent
                    )
                ) {
                    is Handle     -> result
                    is NotHandled -> return result
                }
            }

            is NotRecognized -> return intent
        }

        //translate handle text to speech
        val tts = when (val result = ttsDomain.onSynthesize(
            sessionId = sessionId,
            volume = AppSetting.volume.value,
            siteId = ConfigurationSetting.siteId.value,
            handle = handle,
        )) {
            is Audio          -> result
            is NotSynthesized -> return result
            is Played         -> return result
        }

        indication.onPlayAudio()
        //play audio
        when (val result = sndDomain.awaitPlayAudio(tts)) {
            is Played -> return result
        }
    }

}