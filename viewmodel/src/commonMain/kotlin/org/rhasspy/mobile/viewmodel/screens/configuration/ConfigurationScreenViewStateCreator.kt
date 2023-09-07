package org.rhasspy.mobile.viewmodel.screens.configuration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.Disabled
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.domains.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.domains.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.domains.intenthandling.IIntentHandlingService
import org.rhasspy.mobile.logic.domains.intentrecognition.IIntentRecognitionService
import org.rhasspy.mobile.logic.domains.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.domains.texttospeech.ITextToSpeechService
import org.rhasspy.mobile.logic.domains.wakeword.IWakeWordService
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState.*

class ConfigurationScreenViewStateCreator(
    dispatcherProvider: IDispatcherProvider,
    webServerService: IWebServerConnection,
    mqttService: IMqttConnection,
    private val wakeWordService: IWakeWordService,
    private val speechToTextService: ISpeechToTextService,
    private val intentRecognitionService: IIntentRecognitionService,
    private val textToSpeechService: ITextToSpeechService,
    private val audioPlayingService: IAudioPlayingService,
    private val dialogManagerService: IDialogManagerService,
    private val intentHandlingService: IIntentHandlingService
) {

    private val serviceStateFlow = combineStateFlow(
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
    private val firstErrorIndex =
        serviceStateFlow.mapReadonlyState(sharingStarted = SharingStarted.Eagerly) { array ->
            val index =
                array.indexOfFirst { it is ServiceState.Error || it is ServiceState.Exception }
            return@mapReadonlyState if (index != -1) index else null
        } //TODO
    private val hasError = firstErrorIndex.mapReadonlyState { it != null }

    private val viewState = MutableStateFlow(getViewState(null))

    init {
        CoroutineScope(dispatcherProvider.IO).launch {
            combineStateFlow(
                ConfigurationSetting.siteId.data,
                ConfigurationSetting.dialogManagementOption.data,
                ConfigurationSetting.wakeWordOption.data,
                ConfigurationSetting.speechToTextOption.data,
                ConfigurationSetting.intentRecognitionOption.data,
                ConfigurationSetting.textToSpeechOption.data,
                ConfigurationSetting.audioPlayingOption.data,
                ConfigurationSetting.intentHandlingOption.data,
            ).collect {
                viewState.value = getViewState(viewState.value.scrollToError)
            }
        }
    }

    operator fun invoke(): MutableStateFlow<ConfigurationScreenViewState> = viewState

    private fun getViewState(scrollToError: Int?): ConfigurationScreenViewState {
        return ConfigurationScreenViewState(
            siteId = SiteIdViewState(
                text = ConfigurationSetting.siteId.data
            ),
            dialogPipeline = DialogPipelineViewState(
                dialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
                serviceState = ServiceViewState(dialogManagerService.serviceState)
            ),
            audioInput = AudioInputViewState(
                serviceState = ServiceViewState(MutableStateFlow(Disabled)) //TODO
            ),
            wakeWord = WakeWordViewState(
                wakeWordValueOption = ConfigurationSetting.wakeWordOption.value,
                serviceState = ServiceViewState(wakeWordService.serviceState)
            ),
            speechToText = SpeechToTextViewState(
                speechToTextOption = ConfigurationSetting.speechToTextOption.value,
                serviceState = ServiceViewState(speechToTextService.serviceState)
            ),
            voiceActivityDetection = VoiceActivityDetectionViewState(
                voiceActivityDetectionOption = VoiceActivityDetectionOption.Disabled,
                serviceState = ServiceViewState(MutableStateFlow(Disabled)) //TODO
            ),
            intentRecognition = IntentRecognitionViewState(
                intentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
                serviceState = ServiceViewState(intentRecognitionService.serviceState)
            ),
            intentHandling = IntentHandlingViewState(
                intentHandlingOption = ConfigurationSetting.intentHandlingOption.value,
                serviceState = ServiceViewState(intentHandlingService.serviceState)
            ),
            textToSpeech = TextToSpeechViewState(
                textToSpeechOption = ConfigurationSetting.textToSpeechOption.value,
                serviceState = ServiceViewState(textToSpeechService.serviceState)
            ),
            audioPlaying = AudioPlayingViewState(
                audioPlayingOption = ConfigurationSetting.audioPlayingOption.value,
                serviceState = ServiceViewState(audioPlayingService.serviceState)
            ),
            hasError = hasError,
            firstErrorIndex = firstErrorIndex,
            scrollToError = scrollToError
        )
    }


}