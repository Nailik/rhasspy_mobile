package org.rhasspy.mobile.logic.domains.mic

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import org.rhasspy.mobile.data.domain.MicDomainData
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.domains.mic.MicDomainState.MicrophonePermissionMissing
import org.rhasspy.mobile.logic.domains.mic.MicDomainState.NoError
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission

/**
 * records audio as soon as audioStream has subscribers
 */
internal interface IMicDomain : IDomain {

    val audioStream: Flow<MicAudioChunk>

    val state: StateFlow<MicDomainState>

    val isRecordingState: StateFlow<Boolean>

}

/**
 * records audio as soon as audioStream has subscribers
 */
internal class MicDomain(
    val audioRecorder: IAudioRecorder,
    val microphonePermission: IMicrophonePermission,
    val params: MicDomainData,
) : IMicDomain {

    private val logger = Logger.withTag("MicDomain")

    override var state = MutableStateFlow<MicDomainState>(MicDomainState.Loading)

    override val audioStream = MutableSharedFlow<MicAudioChunk>(extraBufferCapacity = 1)

    override var isRecordingState = MutableStateFlow(false)
    private val isMicrophonePermissionGranted get() = microphonePermission.granted.value

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        state.value = NoError

        scope.launch {
            microphonePermission.granted.collectLatest {
                if (!it) {
                    state.value = MicrophonePermissionMissing
                } else if (state.value == MicrophonePermissionMissing) {
                    state.value = NoError
                }
            }
        }

        audioStream.subscriptionCount
            .map { count -> count != 0 }
            .distinctUntilChanged()
            .onEach { isActive ->
                if (isActive) startRecording() else stopRecording()
            }.launchIn(scope)

        audioRecorder.output.onEach { data ->
            with(params) {
                audioStream.tryEmit(
                    MicAudioChunk(
                        timeStamp = Clock.System.now(),
                        sampleRate = audioOutputSampleRate,
                        encoding = audioOutputEncoding,
                        channel = audioOutputChannel,
                        data = data,
                    )
                )
            }
        }.launchIn(scope)

    }

    private fun startRecording() {
        logger.d { "startRecording permission: $isMicrophonePermissionGranted" }

        if (!isMicrophonePermissionGranted) return

        isRecordingState.value = true

        with(params) {
            audioRecorder.startRecording(
                audioRecorderChannelType = audioInputChannel,
                audioRecorderEncodingType = audioInputEncoding,
                audioRecorderSampleRateType = audioInputSampleRate,
                audioRecorderOutputChannelType = audioOutputChannel,
                audioRecorderOutputEncodingType = audioOutputEncoding,
                audioRecorderOutputSampleRateType = audioOutputSampleRate,
                isAutoPauseOnMediaPlayback = isPauseRecordingOnMediaPlayback,
            )
        }
    }

    private fun stopRecording() {
        logger.d { "stopRecording" }

        audioRecorder.stopRecording()

        isRecordingState.value = false
    }

    override fun dispose() {
        stopRecording()
        scope.cancel()
    }

}