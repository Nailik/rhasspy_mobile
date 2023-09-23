package org.rhasspy.mobile.logic.domains.mic

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import org.rhasspy.mobile.data.domain.MicDomainData
import org.rhasspy.mobile.data.domain.VadDomainData
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.ErrorState.Error
import org.rhasspy.mobile.data.service.ServiceState.Pending
import org.rhasspy.mobile.data.service.ServiceState.Success
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting

/**
 * records audio as soon as audioStream has subscribers
 * Collection may stop for a brief moment when micDomainData is changed
 */
interface IMicDomain : IService {

    val audioStream: Flow<MicAudioChunk>

}

internal class MicDomain(
    val audioRecorder: IAudioRecorder,
    val microphonePermission: IMicrophonePermission,
    val params: MicDomainData, //TODO microphone permision??
) : IMicDomain {

    override val audioStream = MutableSharedFlow<MicAudioChunk>()

    override val serviceState = MutableStateFlow<ServiceState>(Pending)

    private val isMicrophonePermissionGranted get() = microphonePermission.granted.value

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        serviceState.value = when (isMicrophonePermissionGranted) {
            true  -> Success
            false -> Error(MR.strings.microphonePermissionMissing.stable)
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
                isUseAutomaticGainControl = isUseAutomaticGainControl,
                isAutoPauseOnMediaPlayback = isPauseRecordingOnMediaPlayback,
            )
        }
    }

    private fun stopRecording() {
        audioRecorder.stopRecording()
    }

    override fun stop() {
        scope.cancel()
    }

}