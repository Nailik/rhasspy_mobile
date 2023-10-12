package org.rhasspy.mobile.logic.pipeline.impls

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.data.pipeline.PipelineData.LocalPipelineData
import org.rhasspy.mobile.data.pipeline.PipelineData.LocalPipelineData.IndicationSoundOptionData
import org.rhasspy.mobile.data.sounds.IndicationSoundOption
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.pipeline.*
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.IntentResult.Intent
import org.rhasspy.mobile.logic.pipeline.PipelineResult.PipelineErrorResult
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.Transcript
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class PipelineLocal(
    private val params: LocalPipelineData,
    private val domains: DomainBundle,
    private val domainHistory: IDomainHistory,
    private val audioFocus: IAudioFocus,
    private val localAudioPlayer: ILocalAudioPlayer,
) : IPipeline, KoinComponent {

    private val logger = Logger.withTag("PipelineDisabled")
    override suspend fun runPipeline(wakeResult: WakeResult): PipelineResult {

        logger.d { "runPipeline $wakeResult" }

        val sessionId = onStartSession(wakeResult)

        playSound(params.wakeSound)

        return runPipelineInternal(sessionId).also {
            if (it is PipelineErrorResult) {
                playSound(params.errorSound)
            }
        }
    }

    private suspend fun runPipelineInternal(sessionId: String): PipelineResult {

        //transcript audio to text from voice start till voice stop
        val transcript = when (val result = onStartListening(sessionId)) {
            is Transcript          -> result
            is PipelineErrorResult -> return result
        }

        playSound(params.recordedSound)

        //find intent from text, eventually already handles
        val intent = when (val result = onTranscript(transcript)) {
            is Handle,
            is Intent              -> result

            is PipelineErrorResult -> return result
        }

        //handle intent
        val handle = when (intent) {
            is Handle              -> intent
            is Intent              -> {
                when (val result = onIntent(intent)) {
                    is Handle              -> result
                    is PipelineErrorResult -> return result
                }
            }

            is PipelineErrorResult -> return intent
        }

        //translate handle text to speech
        val tts = when (val result = onHandle(handle)) {
            is Audio          -> result
            is PipelineResult -> return result
        }

        //play audio
        return domains.sndDomain.awaitPlayAudio(tts).also {
            domains.dispose()
        }
    }

    private fun onStartSession(wakeResult: WakeResult): String {
        //use session id from event or create own one
        val sessionId = wakeResult.sessionId ?: uuid4().toString()

        domainHistory.addToHistory(
            null,
            PipelineStarted(
                sessionId = sessionId,
                source = Source.Local,
            )
        )
        return sessionId
    }

    private suspend fun onStartListening(sessionId: String): TranscriptResult {
        //transcript audio to text from voice start till voice stop
        return domains.asrDomain.awaitTranscript(
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
    }

    private suspend fun onTranscript(transcript: Transcript): IntentResult {
        //find intent from text, eventually already handles
        return domains.intentDomain.awaitIntent(
            transcript = transcript
        )
    }

    private suspend fun onIntent(intent: Intent): HandleResult {
        //handle intent
        return domains.handleDomain.awaitIntentHandle(
            intent = intent
        )
    }

    private suspend fun onHandle(handle: Handle): TtsResult {
        return domains.ttsDomain.onSynthesize(
            volume = AppSetting.volume.value,
            siteId = ConfigurationSetting.siteId.value,
            handle = handle,
        )
    }

    private suspend fun playSound(indicationSound: IndicationSoundOptionData) {
        if (!params.isSoundIndicationEnabled) return

        val audioSource = when (val option = indicationSound.option) {
            is IndicationSoundOption.Custom   -> AudioSource.File(option.file.toPath())
            is IndicationSoundOption.Default  -> AudioSource.Resource(indicationSound.type.default)
            is IndicationSoundOption.Disabled -> return
        }

        localAudioPlayer.playAudio(
            audioSource = audioSource,
            volume = indicationSound.volume,
            audioOutputOption = params.soundIndicationOutputOption,
        )
    }

}