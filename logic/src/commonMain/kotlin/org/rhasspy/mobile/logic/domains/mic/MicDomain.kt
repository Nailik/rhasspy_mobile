package org.rhasspy.mobile.logic.domains.mic

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
interface IMicDomain : IDomain {

    val audioStream: Flow<MicAudioChunk>

    val state: StateFlow<MicDomainState>

}

/**
 * records audio as soon as audioStream has subscribers
 */
internal class MicDomain(
    val audioRecorder: IAudioRecorder,
    val microphonePermission: IMicrophonePermission,
    val params: MicDomainData,
) : IMicDomain {

    override var state = MutableStateFlow<MicDomainState>(MicDomainState.Loading)

    override val audioStream = MutableSharedFlow<MicAudioChunk>()

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

        scope.launch {
            audioStream.subscriptionCount
                .map { count -> count == 0 }
                .distinctUntilChanged()
                .onEach { isActive ->
                    if (isActive) startRecording() else stopRecording()
                }
        }

        scope.launch {
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
            }
        }
    }

    private fun startRecording() {
        if (!isMicrophonePermissionGranted) return

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
        audioRecorder.stopRecording()
    }

    override fun dispose() {
        stopRecording()
        scope.cancel()
    }

}