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
import org.rhasspy.mobile.logic.pipeline.PipelineEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AsrDomainEvent.*
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AudioDomainEvent.*
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.HandleDomainEvent.HandledEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.HandleDomainEvent.NotHandledEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.IntentDomainEvent.*
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.SndDomainEvent.PlayedEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.TtsDomainEvent.SynthesizeEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.TtsDomainEvent.TtsErrorEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.VadDomainEvent.VoiceStartedEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.VadDomainEvent.VoiceStoppedEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.WakeDomainEvent.DetectionEvent
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
    private val audioFocus: IAudioFocus,
) : PipelineManager(
    indication = indication,
    audioFocus = audioFocus,
    micDomain = micDomain,
) {

    //TODo one domain after another always waiting for result?

    private var coroutineScope = CoroutineScope(dispatcherProvider.IO)

    private val params get() = ConfigurationSetting.pipelineData.value

    override fun initialize() {
        goToState(DetectState)
    }

    //allow to skip thinks (directly say from mqtt/http, directly play wav, directly transcribe etc)
    private fun pipeline() {
        //audio chunks
        wakeDomain.send(micDomain.getAudioChunk()) //detected - not detected
        //if detected
        vadDomain.send(micDomain.getAudioChunk()) //warte auf voice started

            //if voice started
            asrDomain.send(micDomain.getAudioChunkFlow()) //warte auf transcript - transcript error

            //if voice stopped
            micDomain.stopRecording()

            //asrDomain result ??

            //ELSE if voice stopped
            asrDomain.send(micDomain.getAudioStop())


        val result = asrDomain.getResult() //transcript - transcript error

        //if transcript
        intentDomain.send(result)

        //ELSE if transcript error
        RESET()


    }

    override fun onEvent(event: PipelineEvent) {
        with(currentState) {
            when (this) {
                is DetectState     -> onDetectStateEvent(this, event)
                is TranscribeState -> onTranscribeStateEvent(this, event)
                is HandleState     -> onHandleStateEvent(this, event)
                is RecognizeState  -> onRecognizeStateEvent(this, event)
                is SpeakState      -> onSpeakStateEvent(this, event)
                is PlayState       -> onPlayStateEvent(this, event)
            }
        }
    }

    private fun onDetectStateEvent(state: DetectState, event: PipelineEvent) {
        when (event) {
            is AudioChunkEvent -> {
                wakeDomain.onAudioChunk(event)
            }

            is DetectionEvent  -> {
                goToState(
                    TranscribeState(
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

            else               -> Unit
        }
    }

    private fun onTranscribeStateEvent(state: TranscribeState, event: PipelineEvent) {
        when (event) {
            is TranscriptErrorEvent   -> {
                indication.onError()
                goToState(DetectState)
            }

            is TranscriptEvent        -> {
                onEvent(RecognizeEvent(text = event.text))
            }

            is TranscriptTimeoutEvent -> {
                indication.onError()
                goToState(DetectState)
            }

            is AudioChunkEvent        -> {
                vadDomain.onAudioChunk(event)
                asrDomain.onAudioChunk(event, state.sessionData.sessionId)
            }

            is AudioStartEvent        -> {
                asrDomain.onAudioStart(event, state.sessionData.sessionId)
            }

            is AudioStopEvent         -> {
                asrDomain.onAudioStop(event, state.sessionData.sessionId)
            }

            is RecognizeEvent         -> {
                intentDomain.onRecognize(event, state.sessionData.sessionId)

                goToState(
                    RecognizeState(
                        sessionData = state.sessionData,
                        timeoutJob = getIntentTimeoutJob(),
                    )
                )
            }

            is NotRecognizedEvent     -> {
                indication.onError()
                goToState(DetectState)
            }

            is VoiceStartedEvent      -> {
                micDomain.startRecording()
            }

            is VoiceStoppedEvent      -> {
                micDomain.stopRecording()
            }

            else                      -> Unit
        }
    }

    private fun onHandleStateEvent(state: HandleState, event: PipelineEvent) {
        when (event) {
            is HandledEvent    -> {
                goToState(DetectState)
            }

            is NotHandledEvent -> {
                indication.onError()
                goToState(DetectState)
            }

            else               -> Unit
        }
    }

    private fun onRecognizeStateEvent(state: RecognizeState, event: PipelineEvent) {
        when (event) {
            is IntentEvent        -> {
                handleDomain.onIntentEvent(event)
                goToState(
                    HandleState(
                        sessionData = state.sessionData,
                        timeoutJob = getIntentTimeoutJob()
                    )
                )
            }

            is IntentTimeoutEvent -> {
                indication.onError()
                goToState(DetectState)
            }

            else                  -> Unit
        }
    }

    private fun onSpeakStateEvent(state: SpeakState, event: PipelineEvent) {
        when (event) {
            is AudioStartEvent -> {
                sndDomain.onAudioStart(event)
                goToState(SpeakState)
            }

            is TtsErrorEvent   -> {
                goToState(DetectState)
            }

            is SynthesizeEvent -> {
                //TODO
                ttsDomain.onSynthesize(event, "state.se")
            }

            else               -> Unit
        }
    }

    private fun onPlayStateEvent(state: PlayState, event: PipelineEvent) {
        when (event) {
            is AudioChunkEvent -> {
                sndDomain.onAudioChunk(event)
            }

            is AudioStopEvent  -> {
                sndDomain.onAudioStop(event)
            }

            is PlayedEvent     -> {
                goToState(DetectState)
            }

            else               -> Unit
        }
    }


    override fun onEvent(event: MqttConnectionEvent) {
        when (event) {
            is AsrError                -> TranscriptErrorEvent
            is AsrTextCaptured         -> TranscriptEvent(event.text ?: "")
            is EndSession              -> Unit
            is HotWordDetected         -> DetectionEvent(event.hotWord, Clock.System.now())
            is IntentNotRecognized     -> NotRecognizedEvent("")
            is IntentRecognitionResult -> IntentEvent(name = event.intentName, data = event.intent)
            is PlayBytes               -> Unit// AudioChunkEvent
            is PlayFinished            -> PlayedEvent
            is Say                     -> SynthesizeEvent(event.text, event.volume, event.siteId)
            is SessionEnded            -> Unit
            is SessionStarted          -> Unit
            is StartListening          -> Unit//DetectionEvent -> VoiceStartedEvent
            is StartSession            -> Unit//DetectionEvent
            is StopListening           -> Unit//VoiceStoppedEvent
        }
    }

    override fun onEvent(event: WebServerConnectionEvent) {
        when (event) {
            is WebServerListenForCommand -> {
                if (currentState !is DetectState) return
                onEvent(
                    DetectionEvent(
                        name = "WebServerListenForCommand",
                        timeStamp = Clock.System.now()
                    )
                )
            }

            is WebServerPlayWav          -> Unit//AudioChunkEvent
            is WebServerSay              -> Unit//SynthesizeEvent
            is WebServerStartRecording   -> Unit//DetectionEvent -> VoiceStartedEvent
            is WebServerStopRecording    -> Unit//VoiceStoppedEvent
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