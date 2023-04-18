package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.ui.event.StateEvent
import org.rhasspy.mobile.viewmodel.screens.configuration.IConfigurationScreenUiStateEvent.ScrollToErrorEventIState

@Stable
data class ConfigurationScreenViewState internal constructor(
    val siteId: SiteIdViewState = SiteIdViewState(),
    val remoteHermesHttp: RemoteHermesHttpViewState,
    val webserver: WebServerViewState,
    val mqtt: MqttViewState,
    val wakeWord: WakeWordViewState,
    val speechToText: SpeechToTextViewState,
    val intentRecognition: IntentRecognitionViewState,
    val textToSpeech: TextToSpeechViewState,
    val audioPlaying: AudioPlayingViewState,
    val dialogManagement: DialogManagementViewState,
    val intentHandling: IntentHandlingViewState,
    val hasError: StateFlow<Boolean>,
    val firstErrorIndex: StateFlow<Int?>,
    val scrollToErrorEvent: StateFlow<ScrollToErrorEventIState>
) {

    companion object {

        fun getInitialViewState(
            httpClientService: HttpClientService,
            webServerService: WebServerService,
            mqttService: MqttService,
            wakeWordService: WakeWordService,
            speechToTextService: SpeechToTextService,
            intentRecognitionService: IntentRecognitionService,
            textToSpeechService: TextToSpeechService,
            audioPlayingService: AudioPlayingService,
            dialogManagerService: DialogManagerService,
            intentHandlingService: IntentHandlingService
        ) : ConfigurationScreenViewState {
            val serviceStateFlow = combineStateFlow(
                httpClientService.serviceState,
                webServerService.serviceState,
                mqttService.serviceState,
                wakeWordService.serviceState,
                speechToTextService.serviceState,
                intentRecognitionService.serviceState,
                textToSpeechService.serviceState,
                audioPlayingService.serviceState,
                dialogManagerService.serviceState,
                intentHandlingService.serviceState
            )

            return ConfigurationScreenViewState(
                 siteId = SiteIdViewState(),
            remoteHermesHttp = RemoteHermesHttpViewState.getInitialViewState(httpClientService),
                webserver = WebServerViewState.getInitialViewState(webServerService),
                mqtt = MqttViewState.getInitialViewState(mqttService),
                wakeWord = WakeWordViewState.getInitialViewState(wakeWordService),
                speechToText = SpeechToTextViewState.getInitialViewState(speechToTextService),
                intentRecognition = IntentRecognitionViewState.getInitialViewState(intentRecognitionService),
                textToSpeech = TextToSpeechViewState.getInitialViewState(textToSpeechService),
                audioPlaying = AudioPlayingViewState.getInitialViewState(audioPlayingService),
                dialogManagement = DialogManagementViewState.getInitialViewState(dialogManagerService),
                intentHandling = IntentHandlingViewState.getInitialViewState(intentHandlingService),
                hasError = serviceStateFlow.mapReadonlyState { array -> array.firstOrNull { it is ServiceState.Error } != null },
                firstErrorIndex = serviceStateFlow.mapReadonlyState { array -> array.indexOfFirst { it is ServiceState.Error } },
                scrollToErrorEvent = MutableStateFlow(ScrollToErrorEventIState(StateEvent.Consumed, 0))
            )
        }

    }

    @Stable
    data class SiteIdViewState internal constructor(
        val text: String = ConfigurationSetting.siteId.value
    )

    @Stable
    data class RemoteHermesHttpViewState internal constructor(
        val isHttpSSLVerificationEnabled: Boolean,
        val serviceState: ServiceViewState
    ) {
        companion object {
            fun getInitialViewState(service: HttpClientService): RemoteHermesHttpViewState {
                return RemoteHermesHttpViewState(
                    isHttpSSLVerificationEnabled = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value,
                    serviceState = ServiceViewState.getInitialViewState(service)
                )
            }
        }
    }

    @Stable
    data class WebServerViewState internal constructor(
        val isHttpServerEnabled: Boolean,
        val serviceState: ServiceViewState
    ) {
        companion object {
            fun getInitialViewState(service: WebServerService): WebServerViewState {
                return WebServerViewState(
                    isHttpServerEnabled = ConfigurationSetting.isHttpServerEnabled.value,
                    serviceState = ServiceViewState.getInitialViewState(service)
                )
            }
        }
    }

    @Stable
    data class MqttViewState internal constructor(
        val isMQTTConnected: StateFlow<Boolean>,
        val serviceState: ServiceViewState
    ) {
        companion object {
            fun getInitialViewState(service: MqttService): MqttViewState {
                return MqttViewState(
                    isMQTTConnected = service.isConnected,
                    serviceState = ServiceViewState.getInitialViewState(service)
                )
            }
        }
    }

    @Stable
    data class WakeWordViewState internal constructor(
        val wakeWordValueOption: WakeWordOption,
        val serviceState: ServiceViewState
    ) {
        companion object {
            fun getInitialViewState(service: WakeWordService): WakeWordViewState {
                return WakeWordViewState(
                    wakeWordValueOption = ConfigurationSetting.wakeWordOption.value,
                    serviceState = ServiceViewState.getInitialViewState(service)
                )
            }
        }
    }

    @Stable
    data class SpeechToTextViewState internal constructor(
        val speechToTextOption: SpeechToTextOption,
        val serviceState: ServiceViewState
    ) {
        companion object {
            fun getInitialViewState(service: SpeechToTextService): SpeechToTextViewState {
                return SpeechToTextViewState(
                    speechToTextOption = ConfigurationSetting.speechToTextOption.value,
                    serviceState = ServiceViewState.getInitialViewState(service)
                )
            }
        }
    }

    @Stable
    data class IntentRecognitionViewState internal constructor(
        val intentRecognitionOption: IntentRecognitionOption,
        val serviceState: ServiceViewState
    ) {
        companion object {
            fun getInitialViewState(service: IntentRecognitionService): IntentRecognitionViewState {
                return IntentRecognitionViewState(
                    intentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
                    serviceState = ServiceViewState.getInitialViewState(service)
                )
            }
        }
    }

    @Stable
    data class TextToSpeechViewState internal constructor(
        val textToSpeechOption: TextToSpeechOption,
        val serviceState: ServiceViewState
    ) {
        companion object {
            fun getInitialViewState(service: TextToSpeechService): TextToSpeechViewState {
                return TextToSpeechViewState(
                    textToSpeechOption = ConfigurationSetting.textToSpeechOption.value,
                    serviceState = ServiceViewState.getInitialViewState(service)
                )
            }
        }
    }

    @Stable
    data class AudioPlayingViewState internal constructor(
        val audioPlayingOption: AudioPlayingOption,
        val serviceState: ServiceViewState
    ) {
        companion object {
            fun getInitialViewState(service: AudioPlayingService): AudioPlayingViewState {
                return AudioPlayingViewState(
                    audioPlayingOption = ConfigurationSetting.audioPlayingOption.value,
                    serviceState = ServiceViewState.getInitialViewState(service)
                )
            }
        }
    }

    @Stable
    data class DialogManagementViewState internal constructor(
        val dialogManagementOption: DialogManagementOption,
        val serviceState: ServiceViewState
    ) {
        companion object {
            fun getInitialViewState(service: DialogManagerService): DialogManagementViewState {
                return DialogManagementViewState(
                    dialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
                    serviceState = ServiceViewState.getInitialViewState(service)
                )
            }
        }
    }

    @Stable
    data class IntentHandlingViewState internal constructor(
        val intentHandlingOption: IntentHandlingOption,
        val serviceState: ServiceViewState
    ) {
        companion object {
            fun getInitialViewState(service: IntentHandlingService): IntentHandlingViewState {
                return IntentHandlingViewState(
                    intentHandlingOption = ConfigurationSetting.intentHandlingOption.value,
                    serviceState = ServiceViewState.getInitialViewState(service)
                )
            }
        }
    }

}