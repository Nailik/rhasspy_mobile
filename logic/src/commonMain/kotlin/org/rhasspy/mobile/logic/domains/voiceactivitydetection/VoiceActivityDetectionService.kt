package org.rhasspy.mobile.logic.domains.voiceactivitydetection

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.readOnly

interface IVoiceActivityDetectionService : IService {

    fun start()
    fun stop()

}

internal class VoiceActivityDetectionService(
    paramsCreator: VoiceActivityDetectionParamsCreator,
    private val audioRecorder: IAudioRecorder
) : IVoiceActivityDetectionService {

    private val logger = LogType.VoiceActivityDetectionService.logger()

    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val paramsFlow: StateFlow<VoiceActivityDetectionParams> = paramsCreator()
    private val params: VoiceActivityDetectionParams get() = paramsFlow.value

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    override val serviceState = _serviceState.readOnly

    private val scope = CoroutineScope(Dispatchers.IO)
    private var detection: Job? = null

    private var isDetectionRunning = false

    init {
        scope.launch {
            paramsFlow.collect {
                detection?.cancel()
                detection = null

                updateState(it.voiceActivityDetectionOption)

                if (isDetectionRunning) {
                    start()
                }
            }
        }
    }

    private fun updateState(voiceActivityDetectionOption: VoiceActivityDetectionOption) {
        _serviceState.value = when (voiceActivityDetectionOption) {
            VoiceActivityDetectionOption.Local    -> ServiceState.Success
            VoiceActivityDetectionOption.Disabled -> ServiceState.Disabled
        }
    }

    override fun start() {
        isDetectionRunning = true
        stop()
        detection = scope.launch {

            when (params.voiceActivityDetectionOption) {
                VoiceActivityDetectionOption.Local    -> {
                    val localSilenceDetection = SilenceDetection(
                        automaticSilenceDetectionTime = params.automaticSilenceDetectionTime,
                        automaticSilenceDetectionMinimumTime = params.automaticSilenceDetectionMinimumTime,
                        automaticSilenceDetectionAudioLevel = params.automaticSilenceDetectionAudioLevel,
                    ) {
                        serviceMiddleware.action(ServiceMiddlewareAction.DialogServiceMiddlewareAction.SilenceDetected(Source.Local))
                    }

                    audioRecorder.maxVolume.collect {
                        localSilenceDetection.audioFrameVolume(it)
                    }
                }

                VoiceActivityDetectionOption.Disabled -> Unit
            }

        }
    }

    override fun stop() {
        isDetectionRunning = false
        detection?.cancel()
        detection = null
    }


}