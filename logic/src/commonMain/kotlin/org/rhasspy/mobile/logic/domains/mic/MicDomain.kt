package org.rhasspy.mobile.logic.domains.mic

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import org.rhasspy.mobile.data.domain.MicDomainData
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.viewstate.TextWrapper
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperStableStringResource
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.resources.MR

/**
 * records audio as soon as audioStream has subscribers
 */
interface IMicDomain : IDomain {

    val audioStream: Flow<MicAudioChunk>

    val hasError: StateFlow<TextWrapper?>

}

/**
 * records audio as soon as audioStream has subscribers
 */
internal class MicDomain(
    val audioRecorder: IAudioRecorder,
    val microphonePermission: IMicrophonePermission,
    val params: MicDomainData,
) : IMicDomain {

    override var hasError = MutableStateFlow<TextWrapper?>(null)

    override val audioStream = MutableSharedFlow<MicAudioChunk>()

    private val isMicrophonePermissionGranted get() = microphonePermission.granted.value

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            microphonePermission.granted.collectLatest {
                hasError.value = if (!it) {
                    TextWrapperStableStringResource(MR.strings.microphonePermissionMissing.stable)
                } else {
                    null
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
                isUseLoudnessEnhancer = isUseLoudnessEnhancer,
                gainControl = gainControl,
                isAutoPauseOnMediaPlayback = isPauseRecordingOnMediaPlayback,
            )
        }
    }

    private fun stopRecording() {
        audioRecorder.stopRecording()
    }

    override fun dispose() {
        scope.cancel()
    }

}