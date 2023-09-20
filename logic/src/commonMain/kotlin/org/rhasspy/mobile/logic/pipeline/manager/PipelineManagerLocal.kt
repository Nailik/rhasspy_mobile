package org.rhasspy.mobile.logic.pipeline.manager

import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.*
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent.*
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.handle.IHandleDomain
import org.rhasspy.mobile.logic.domains.intent.IIntentDomain
import org.rhasspy.mobile.logic.domains.mic.IMicDomain
import org.rhasspy.mobile.logic.domains.snd.ISndDomain
import org.rhasspy.mobile.logic.domains.tts.ITtsDomain
import org.rhasspy.mobile.logic.domains.vad.IVadDomain
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AsrDomainEvent.*
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AudioDomainEvent.*
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.HandleDomainEvent.HandledEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.HandleDomainEvent.NotHandledEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.IntentDomainEvent.*
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.SndDomainEvent.PlayedEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.TtsDomainEvent.SynthesizeEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.TtsDomainEvent.TtsErrorEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.VadDomainEvent.*
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.WakeDomainEvent.DetectionEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.WakeDomainEvent.NotDetectedEvent
import org.rhasspy.mobile.logic.pipeline.PipelineState.*
import org.rhasspy.mobile.logic.pipeline.PipelineState.SessionState.*
import org.rhasspy.mobile.logic.pipeline.SessionData
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.settings.ConfigurationSetting
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class PipelineManagerLocal(
    dispatcherProvider: IDispatcherProvider,
    private val wakeDomain: IWakeDomain,
    private val asrDomain: IAsrDomain,
    private val handleDomain: IHandleDomain,
    private val intentDomain: IIntentDomain,
    private val micDomain: IMicDomain,
    private val sndDomain: ISndDomain,
    private val ttsDomain: ITtsDomain,
    private val vadDomain: IVadDomain,
    private val indication: IIndication,
    private val localAudioPlayer: ILocalAudioPlayer,
    private val audioFocus: IAudioFocus,
) : PipelineManager(
    indication = indication,
    localAudioPlayer = localAudioPlayer,
    audioFocus = audioFocus,
    micDomain = micDomain,
) {

    private var coroutineScope = CoroutineScope(dispatcherProvider.IO)

    private val params get() = ConfigurationSetting.pipelineData.value

    override fun initialize() {
        goToState(IdleState)
    }

    fun onTranscriptErrorEvent(event: TranscriptErrorEvent) {
        if (currentState !is AsrState) return

        indication.onError()
        goToState(IdleState)
    }

    fun onTranscriptEvent(event: TranscriptEvent) {
        val state = currentState
        if (state !is AsrState) return

        onRecognizeEvent(
            RecognizeEvent(
                text = event.text,
                sessionId = state.sessionData.sessionId,
            )
        )
    }

    fun onTranscriptTimeoutEvent(event: TranscriptTimeoutEvent) {
        if (currentState !is AsrState) return

        indication.onError()
        goToState(IdleState)
    }

    fun onAudioChunkEvent(event: AudioChunkEvent) {
        when (currentState) {
            is IdleState -> {
                wakeDomain.onAudioChunk(event)
            }

            is AsrState  -> {
                vadDomain.onAudioChunk(event)
                asrDomain.onAudioChunk(event)
            }

            SndState     -> {
                sndDomain.onAudioChunk(event)
            }

            else         -> Unit
        }
    }

    fun onAudioStartEvent(event: AudioStartEvent) {
        when (currentState) {
            is TtsState -> {
                sndDomain.onAudioStart(event)
                goToState(SndState)
            }

            is AsrState -> {
                asrDomain.onAudioStart(event)
            }

            else        -> Unit
        }
    }

    fun onAudioStopEvent(event: AudioStopEvent) {
        when (currentState) {
            is SndState -> {
                sndDomain.onAudioStop(event)
            }

            is AsrState -> {
                asrDomain.onAudioStop(event)
            }

            else        -> Unit
        }
    }

    fun onHandledEvent(event: HandledEvent) {
        if (currentState !is IntentState) return

        goToState(IdleState)
    }

    fun onNotHandledEvent(event: NotHandledEvent) {
        if (currentState !is IntentState) return

        indication.onError()
        goToState(IdleState)
    }

    fun onIntentEvent(event: IntentEvent) {
        val state = currentState
        if (state !is IntentState) return

        handleDomain.onIntentEvent(event)
        goToState(
            HandleState(
                sessionData = state.sessionData,
                timeoutJob = getIntentTimeoutJob()
            )
        )
    }

    fun onIntentTimeoutEvent(event: IntentTimeoutEvent) {
        indication.onError()
        goToState(IdleState)
    }

    fun onNotRecognizedEvent(event: NotRecognizedEvent) {
        val state = currentState
        if (state !is AsrState) return

        indication.onError()
        goToState(IdleState)
    }

    fun onRecognizeEvent(event: RecognizeEvent) {
        val state = currentState
        if (state !is AsrState) return

        intentDomain.onRecognize(event)

        goToState(
            IntentState(
                sessionData = state.sessionData,
                timeoutJob = getIntentTimeoutJob(),
            )
        )
    }

    fun onPlayedEvent(event: PlayedEvent) {
        if (currentState !is SndState) return
        goToState(IdleState)
    }

    fun onSynthesizeEvent(event: SynthesizeEvent) {
        if (currentState !is IdleState) return
        ttsDomain.onSynthesize(event)
        goToState(TtsState)
    }

    fun onTtsErrorEvent(event: TtsErrorEvent) {
        if (currentState !is TtsState) return
        indication.onError()
        goToState(IdleState)
    }

    fun onVadTimeoutEvent(event: VadTimeoutEvent) {
        indication.onError()
        goToState(IdleState)
    }

    fun onVoiceStartedEvent(event: VoiceStartedEvent) {
        val state = currentState
        if (state !is AsrState) return

        asrDomain.onAudioStop(
            AudioStopEvent(
                sessionId = state.sessionData.sessionId,
                timeStamp = Clock.System.now()
            )
        )
    }

    fun onVoiceStoppedEvent(event: VoiceStoppedEvent) {

    }

    fun onDetectionEvent(event: DetectionEvent) {
        if (currentState !is IdleState) return

        goToState(
            AsrState(
                sessionData = SessionData(
                    sessionId = uuid4().toString(),
                    sendAudioCaptured = false,
                    wakeWord = event.name,
                    recognizedText = null
                ),
                timeoutJob = getAsrTimeoutJob()
            )
        )
    }

    fun onNotDetectedEvent(event: NotDetectedEvent) {
        if (currentState !is IdleState) return

        indication.onError()
        goToState(IdleState)
    }

    override fun onEvent(event: MqttConnectionEvent) {
        when (event) {
            is AsrError                -> TODO()
            is AsrTextCaptured         -> TODO()
            is EndSession              -> TODO()
            is HotWordDetected         -> TODO()
            is IntentNotRecognized     -> TODO()
            is IntentRecognitionResult -> TODO()
            is PlayBytes               -> TODO()
            is PlayFinished            -> TODO()
            is Say                     -> TODO()
            is SessionEnded            -> TODO()
            is SessionStarted          -> TODO()
            is StartListening          -> TODO()
            is StartSession            -> TODO()
            is StopListening           -> TODO()
        }
    }

    override fun onEvent(event: WebServerConnectionEvent) {
        when (event) {
            is WebServerListenForCommand -> TODO()
            is WebServerListenForWake    -> TODO()
            is WebServerPlayRecording    -> TODO()
            is WebServerPlayWav          -> TODO()
            is WebServerSay              -> TODO()
            is WebServerStartRecording   -> TODO()
            is WebServerStopRecording    -> TODO()
        }
    }

    private fun getAsrTimeoutJob(): Job {
        return coroutineScope.launch {
            if (params.asrDomainTimeout == 0L) return@launch
            delay(params.asrDomainTimeout.toDuration(DurationUnit.MILLISECONDS))
            onEvent(TranscriptTimeoutEvent)
        }
    }

    private fun getIntentTimeoutJob(): Job {
        return coroutineScope.launch {
            if (params.intentDomainTimeout == 0L) return@launch
            delay(params.intentDomainTimeout.toDuration(DurationUnit.MILLISECONDS))
            onEvent(IntentTimeoutEvent)
        }
    }

}