package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSettings

class AutomaticSilenceDetectionSettingsViewModel : ViewModel() {

    //unsaved data
    private val _isAutomaticSilenceDetectionEnabled = MutableStateFlow(AppSettings.isAutomaticSilenceDetectionEnabled.value)
    private val _automaticSilenceDetectionTime = MutableStateFlow(AppSettings.automaticSilenceDetectionTime.value)
    private val _automaticSilenceDetectionAudioLevel = MutableStateFlow(AppSettings.automaticSilenceDetectionAudioLevel.value)

    //unsaved ui data
    val isAutomaticSilenceDetectionEnabled = _isAutomaticSilenceDetectionEnabled.readOnly
    val isSilenceDetectionSettingsVisible = _isAutomaticSilenceDetectionEnabled.readOnly
    val automaticSilenceDetectionTime = _automaticSilenceDetectionTime.readOnly
    val automaticSilenceDetectionAudioLevel = _automaticSilenceDetectionAudioLevel.readOnly

    //testing
    private val _currentAudioLevel = MutableStateFlow<Byte>(0)
    private val _currentStatus = MutableStateFlow(false)
    val currentAudioLevel = _currentAudioLevel.readOnly
    val currentStatus = _currentStatus.readOnly

    //testing running
    private var job: Job? = null

    //set new intent recognition option
    fun toggleAutomaticSilenceDetectionEnabled(enabled: Boolean) {
        _isAutomaticSilenceDetectionEnabled.value = enabled
    }

    //update time for automatic silence detection
    fun updateAutomaticSilenceDetectionTime(time: String) {
        time.replace("-", "")
            .replace(",", "")
            .replace(".", "")
            .replace(" ", "")
            .toIntOrNull()?.also {
                _automaticSilenceDetectionTime.value = it
            }
    }

    //update audio detection level for automatic silence detection
    fun updateAutomaticSilenceDetectionAudioLevel(audioLevel: String) {
        audioLevel.replace("-", "")
            .replace(",", "")
            .replace(".", "")
            .replace(" ", "")
            .toIntOrNull()?.also {
                _automaticSilenceDetectionTime.value = it
            }
    }

    fun toggleAudioLevelTest() {
        if (currentStatus.value) {
            stopAudioLevelTest()
        } else {
            startAudioLevelTest()
        }
    }

    private fun startAudioLevelTest() {
        _currentAudioLevel.value = 0
        _currentStatus.value = true

        job = CoroutineScope(Dispatchers.Default).launch {
            AudioRecorder.output.collectIndexed { _, value ->
                var max: Byte = 0
                value.forEach {
                    if (it >= max || it <= -max) {
                        max = it
                    }
                }
                viewModelScope.launch {
                    _currentAudioLevel.value = max
                }
            }
        }

        AudioRecorder.startRecording()
    }

    private fun stopAudioLevelTest() {
        AudioRecorder.stopRecording()
        job?.cancel()

        _currentStatus.value = false
    }

    /**
     * save data configuration
     */
    fun save() {
        AppSettings.isAutomaticSilenceDetectionEnabled.value = _isAutomaticSilenceDetectionEnabled.value
        AppSettings.automaticSilenceDetectionTime.value = _automaticSilenceDetectionTime.value
        AppSettings.automaticSilenceDetectionAudioLevel.value = _automaticSilenceDetectionAudioLevel.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}