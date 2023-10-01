package org.rhasspy.mobile.logic.pipeline

import com.benasher44.uuid.uuid4
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.handle.IHandleDomain
import org.rhasspy.mobile.logic.domains.intent.IIntentDomain
import org.rhasspy.mobile.logic.domains.mic.IMicDomain
import org.rhasspy.mobile.logic.domains.snd.ISndDomain
import org.rhasspy.mobile.logic.domains.tts.ITtsDomain
import org.rhasspy.mobile.logic.domains.vad.IVadDomain
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.pipeline.HandleResult.*
import org.rhasspy.mobile.logic.pipeline.IntentResult.*
import org.rhasspy.mobile.logic.pipeline.SndResult.*
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.*
import org.rhasspy.mobile.logic.pipeline.TtsResult.*
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IPipelineLocal : IPipeline

class PipelineLocal(
    private val asrDomain: IAsrDomain,
    private val handleDomain: IHandleDomain,
    private val intentDomain: IIntentDomain,
    private val micDomain: IMicDomain,
    private val sndDomain: ISndDomain,
    private val ttsDomain: ITtsDomain,
    private val vadDomain: IVadDomain,
    private val audioFocus: IAudioFocus,
) : IPipelineLocal {

    override suspend fun runPipeline(startEvent: StartEvent): PipelineResult {

        //use session id from event or create own one
        val sessionId = startEvent.sessionId ?: uuid4().toString()

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
            is Transcript         -> result
            is TranscriptError    -> return result
            is TranscriptDisabled -> return result
            is TranscriptTimeout  -> return result
        }

        //find intent from text, eventually already handles
        val intent = intentDomain.awaitIntent(
            sessionId = sessionId,
            transcript = transcript
        )

        //handle intent
        val handle = when (intent) {
            is Handle         -> intent
            is NotHandled     -> return intent
            is Intent         -> {
                when (
                    val result = handleDomain.awaitIntentHandle(
                        sessionId = sessionId,
                        intent = intent
                    )
                ) {
                    is Handle         -> result
                    is NotHandled     -> return result
                    is HandleDisabled -> return result
                }
            }

            is NotRecognized  -> return intent
            is HandleDisabled -> return intent
            is IntentDisabled -> return intent
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
            is TtsDisabled    -> return result
        }

        //play audio
        return when (val result = sndDomain.awaitPlayAudio(tts)) {
            is Played       -> result
            is NotPlayed    -> result
            is PlayDisabled -> result
        }.also {
            asrDomain.dispose()
            handleDomain.dispose()
            intentDomain.dispose()
            micDomain.dispose()
            sndDomain.dispose()
            ttsDomain.dispose()
            vadDomain.dispose()
        }
    }

}