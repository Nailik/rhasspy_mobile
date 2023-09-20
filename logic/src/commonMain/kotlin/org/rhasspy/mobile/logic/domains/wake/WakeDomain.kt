package org.rhasspy.mobile.logic.domains.wake

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.pipeline.IPipeline
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AudioDomainEvent.AudioChunkEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.WakeDomainEvent.DetectionEvent
import org.rhasspy.mobile.platformspecific.porcupine.PorcupineWakeWordClient
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IWakeDomain : IService {

    fun onAudioChunk(chunk: AudioChunkEvent)

}

/**
 * hot word services listens for hot word, evaluates configuration settings but no states
 *
 * calls stateMachineService when hot word was detected
 */
internal class WakeDomain(
    private val pipeline: IPipeline
) : IWakeDomain {

    private val logger = Logger.withTag("WakeWordService")

    override val serviceState = MutableStateFlow<ServiceState>(Pending)
    private val params get() = ConfigurationSetting.wakeDomainData.value

    private val scope = CoroutineScope(Dispatchers.IO)

    private var porcupineWakeWordClient = PorcupineWakeWordClient(
        wakeWordPorcupineAccessToken = params.wakeWordPorcupineAccessToken,
        wakeWordPorcupineKeywordDefaultOptions = params.wakeWordPorcupineKeywordDefaultOptions,
        wakeWordPorcupineKeywordCustomOptions = params.wakeWordPorcupineKeywordCustomOptions,
        wakeWordPorcupineLanguage = params.wakeWordPorcupineLanguage,
        onKeywordDetected = ::onKeywordDetected,
    )

    private var udpConnection = UdpConnection(
        params.wakeWordUdpOutputHost,
        params.wakeWordUdpOutputPort
    )

    /**
     * starts the service
     */
    init {
        scope.launch {
            ConfigurationSetting.wakeDomainData.data.collectLatest {
                initialize()
            }
        }
    }

    private fun initialize() {
        serviceState.value = when (params.wakeWordOption) {
            WakeWordOption.Porcupine          -> initializePorcupine()
            WakeWordOption.Rhasspy2HermesMQTT -> Success
            WakeWordOption.Udp                -> initializeUdp()
            WakeWordOption.Disabled           -> Disabled
        }
    }

    private fun initializePorcupine(): ServiceState {
        porcupineWakeWordClient.close()
        porcupineWakeWordClient = PorcupineWakeWordClient(
            wakeWordPorcupineAccessToken = params.wakeWordPorcupineAccessToken,
            wakeWordPorcupineKeywordDefaultOptions = params.wakeWordPorcupineKeywordDefaultOptions,
            wakeWordPorcupineKeywordCustomOptions = params.wakeWordPorcupineKeywordCustomOptions,
            wakeWordPorcupineLanguage = params.wakeWordPorcupineLanguage,
            onKeywordDetected = ::onKeywordDetected,
        )

        return when (val result = porcupineWakeWordClient.initialize()) {
            is Exception -> ErrorState.Exception(result)
            else         -> Success
        }
    }

    private fun initializeUdp(): ServiceState {
        udpConnection.close()
        udpConnection = UdpConnection(
            params.wakeWordUdpOutputHost,
            params.wakeWordUdpOutputPort
        )

        return when (val result = udpConnection.connect()) {
            is Exception -> ErrorState.Exception(result)
            else         -> Success
        }
    }

    override fun onAudioChunk(chunk: AudioChunkEvent) {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "hotWordAudioFrame dataSize: ${chunk.data.size}" }
        }

        with(chunk) {
            when (params.wakeWordOption) {
                WakeWordOption.Porcupine          ->
                    porcupineWakeWordClient.audioFrame(
                        sampleRate = sampleRate,
                        encoding = encoding,
                        channel = channel,
                        data = data
                    )

                WakeWordOption.Rhasspy2HermesMQTT -> Unit //nothing will wait for mqtt message
                WakeWordOption.Udp                ->
                    scope.launch {
                        udpConnection.streamAudio(
                            sampleRate = sampleRate,
                            encoding = encoding,
                            channel = channel,
                            data = data
                        )
                    }

                WakeWordOption.Disabled           -> Unit
            }
        }
    }

    /**
     * local wake word was detected
     */
    private fun onKeywordDetected(name: String) {
        logger.d { "onKeywordDetected $name" }
        pipeline.onEvent(
            DetectionEvent(
                name = name,
                timeStamp = Clock.System.now()
            )
        )
    }

}