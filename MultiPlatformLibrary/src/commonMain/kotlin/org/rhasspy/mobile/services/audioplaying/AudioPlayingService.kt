package org.rhasspy.mobile.services.audioplaying

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.Action.DialogAction
import org.rhasspy.mobile.middleware.ServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.httpclient.HttpClientResult
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.settings.option.AudioPlayingOption

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class AudioPlayingService : IService() {
    private val logger = LogType.AudioPlayingService.logger()

    private val params by inject<AudioPlayingServiceParams>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success())
    val serviceState = _serviceState.readOnly

    private val localAudioService by inject<LocalAudioService>()
    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()

    private val serviceMiddleware by inject<ServiceMiddleware>()

    /**
     * hermes/audioServer/<siteId>/playBytes/<requestId>
     * Play WAV data
     *
     * Response(s)
     * hermes/audioServer/<siteId>/playFinished (JSON)
     *
     * - if audio output is enabled
     *
     * Local:
     * - play audio with volume set
     *
     * HTTP:
     * - calls service to play audio with wav data
     *
     * MQTT:
     * - calls default site to play audio
     */
    suspend fun playAudio(data: List<Byte>) {
        logger.d { "playAudio dataSize: ${data.size}" }
        when (params.audioPlayingOption) {
            AudioPlayingOption.Local -> {
                _serviceState.value = localAudioService.playAudio(data)?.let {
                    ServiceState.Error(it)
                } ?: run {
                    ServiceState.Success()
                }
                serviceMiddleware.action(DialogAction.PlayFinished(Source.Local))
            }
            AudioPlayingOption.RemoteHTTP -> {
                val result = httpClientService.playWav(data)
                _serviceState.value = when (result) {
                    is HttpClientResult.Error -> ServiceState.Error(result.exception)
                    is HttpClientResult.Success -> ServiceState.Success()
                }
                serviceMiddleware.action(DialogAction.PlayFinished(Source.HttpApi))
            }
            AudioPlayingOption.RemoteMQTT -> {
                _serviceState.value = mqttClientService.playBytes(data)?.let {
                    ServiceState.Error(Throwable(it.msg))
                } ?: run {
                    ServiceState.Success()
                }
            }
            AudioPlayingOption.Disabled -> {}
        }
    }

}