package org.rhasspy.mobile.logic.domains.mic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.ServiceState.ErrorState.Error
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.pipeline.IPipeline
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AudioDomainEvent.AudioChunkEvent
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IMicDomain : IService {

    fun startRecording()

    fun stopRecording()

}

internal class MicDomain(
    private val pipeline: IPipeline,
    val audioRecorder: IAudioRecorder,
    val microphonePermission: IMicrophonePermission
) : IMicDomain {

    override val serviceState = MutableStateFlow<ServiceState>(Pending)
    private val params get() = ConfigurationSetting.micDomainData.value
    private val isMicrophonePermissionGranted get() = microphonePermission.granted.value

    private val scope = CoroutineScope(Dispatchers.IO)

    private var shouldRecord: Boolean = false

    init {
        scope.launch {
            combineStateFlow(
                ConfigurationSetting.micDomainData.data,
                microphonePermission.granted,
            ).collectLatest {
                stopRecording()
                if (shouldRecord) {
                    stopRecording()
                }

                initialize()
            }
        }

        scope.launch {
            audioRecorder.output.collect { data ->
                pipeline.onEvent(getAudioChunk(data))
            }
        }
    }

    private fun initialize() {
        serviceState.value = when (isMicrophonePermissionGranted) {
            true  -> Success
            false -> Error(MR.strings.microphonePermissionMissing.stable)
        }
    }

    override fun startRecording() {
        if (!isMicrophonePermissionGranted) return

        shouldRecord = true
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

        pipeline.onEvent(getAudioChunk(data))
    }

    private fun getAudioChunk(data: ByteArray): AudioChunkEvent {
        with(params) {
            return AudioChunkEvent(
                timeStamp = Clock.System.now(),
                sampleRate = audioOutputSampleRate,
                encoding = audioOutputEncoding,
                channel = audioOutputChannel,
                data = data,
            )
        }
    }

    override fun stopRecording() {
        shouldRecord = false
        audioRecorder.stopRecording()
    }


}