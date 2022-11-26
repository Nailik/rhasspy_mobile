package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.*
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.middleware.EventType
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.reflect.KClass

class SpeechToTextConfigurationViewModel : IConfigurationViewModel() {

    //unsaved data
    private val _speechToTextOption = MutableStateFlow(ConfigurationSettings.speechToTextOption.value)
    private val _isUseCustomSpeechToTextHttpEndpoint = MutableStateFlow(ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value)
    private val _speechToTextHttpEndpoint = MutableStateFlow(ConfigurationSettings.speechToTextHttpEndpoint.value)

    //unsaved ui data
    val speechToTextOption = _speechToTextOption.readOnly
    val speechToTextHttpEndpoint = combineState(_isUseCustomSpeechToTextHttpEndpoint, _speechToTextHttpEndpoint) { useCustomSpeechToTextHttpEndpoint,
                                                                                                                   speechToTextHttpEndpoint ->
        if (useCustomSpeechToTextHttpEndpoint) {
            speechToTextHttpEndpoint
        } else {
            "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.SpeechToText}"
        }
    }
    val isUseCustomSpeechToTextHttpEndpoint = _isUseCustomSpeechToTextHttpEndpoint.readOnly
    val isSpeechToTextHttpEndpointChangeEnabled = isUseCustomSpeechToTextHttpEndpoint

    override val isTestingEnabled = _speechToTextOption.mapReadonlyState { it != SpeechToTextOptions.Disabled }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_speechToTextOption, ConfigurationSettings.speechToTextOption.data),
        combineStateNotEquals(_isUseCustomSpeechToTextHttpEndpoint, ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.data),
        combineStateNotEquals(_speechToTextHttpEndpoint, ConfigurationSettings.speechToTextHttpEndpoint.data)
    )

    //show endpoint settings
    fun isSpeechToTextHttpSettingsVisible(option: SpeechToTextOptions): Boolean {
        return option == SpeechToTextOptions.RemoteHTTP
    }

    //all options
    val speechToTextOptions = SpeechToTextOptions::values

    //set new speech to text option
    fun selectSpeechToTextOption(option: SpeechToTextOptions) {
        _speechToTextOption.value = option
    }

    //toggle if custom endpoint is used
    fun toggleUseCustomHttpEndpoint(enabled: Boolean) {
        _isUseCustomSpeechToTextHttpEndpoint.value = enabled
    }

    //set new speech to text http endpoint
    fun updateSpeechToTextHttpEndpoint(endpoint: String) {
        _speechToTextHttpEndpoint.value = endpoint
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSettings.speechToTextOption.value = _speechToTextOption.value
        ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value = _isUseCustomSpeechToTextHttpEndpoint.value
        ConfigurationSettings.speechToTextHttpEndpoint.value = _speechToTextHttpEndpoint.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _speechToTextOption.value = ConfigurationSettings.speechToTextOption.value
        _isUseCustomSpeechToTextHttpEndpoint.value = ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value
        _speechToTextHttpEndpoint.value = ConfigurationSettings.speechToTextHttpEndpoint.value
    }

    /**
     * test unsaved data configuration
     */
    override fun onTest() {
        //initialize test params
        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomSpeechToTextHttpEndpoint = _isUseCustomSpeechToTextHttpEndpoint.value,
                    speechToTextHttpEndpoint = _speechToTextHttpEndpoint.value
                )
            )
        }
        get<RhasspyActionsServiceParams> {
            parametersOf(
                RhasspyActionsServiceParams(
                    speechToTextOption = _speechToTextOption.value
                )
            )
        }
        get<MqttService>()
        //start web server
        get<RhasspyActionsService>()
    }

    private var testScope = CoroutineScope(Dispatchers.Default)

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly



    //for test
    override val evenFilterType: KClass<*> = EventType.MqttServiceEventType::class

    override fun onStopTest() {
        _isRecording.value = false
        testScope.cancel()
    }

    override suspend fun runTest() {
        //TODO test with real audio??

        val service = get<MqttService>()
        CoroutineScope(Dispatchers.Default).launch {
            service.isHasStarted.collect {
                //allow record button
            }
        }
        super.runTest()
    }

    fun startTestRecording() {
        val service = get<MqttService>()

        if (!_isRecording.value) {
            _isRecording.value = true
            testScope = CoroutineScope(Dispatchers.Default)
            testScope.launch {
                service.hotWordDetected("test")
                //await start listening
                AudioRecorder.output.collect {
                    if (_isRecording.value) {
                        service.audioFrame(it.toMutableList().addWavHeader())
                    }
                }
            }

            AudioRecorder.startRecording()
        } else {
            testScope.launch {
                AudioRecorder.output.collect {
                    if (!_isRecording.value) {
                        //send silence to force stop recording
                        //Works, fake silence
                        service.audioFrame(it.map { 0.toByte() }.toMutableList().addWavHeader())
                    }
                }
            }
        }

    }

}