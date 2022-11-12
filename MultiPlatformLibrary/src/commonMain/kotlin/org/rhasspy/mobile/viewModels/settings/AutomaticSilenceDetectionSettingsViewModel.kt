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

    //unsaved ui data
    val isAutomaticSilenceDetectionEnabled = AppSettings.isAutomaticSilenceDetectionEnabled.data
    val isSilenceDetectionSettingsVisible = isAutomaticSilenceDetectionEnabled
    val automaticSilenceDetectionTime = AppSettings.automaticSilenceDetectionTime.data
    val automaticSilenceDetectionAudioLevel = AppSettings.automaticSilenceDetectionAudioLevel.data

    //testing
    private val _currentAudioLevel = MutableStateFlow<Byte>(0)
    private val _isRecording = MutableStateFlow(false)
    val currentAudioLevel = _currentAudioLevel.readOnly
    val isRecording = _isRecording.readOnly

    //testing running
    private var job: Job? = null

    //set new intent recognition option
    fun toggleAutomaticSilenceDetectionEnabled(enabled: Boolean) {
        AppSettings.isAutomaticSilenceDetectionEnabled.value  = enabled
    }

    //update time for automatic silence detection
    fun updateAutomaticSilenceDetectionTime(time: String) {
        time.replace("""[-,. ]""".toRegex(), "")
            .toIntOrNull()?.also {
                AppSettings.automaticSilenceDetectionTime.value = it
            }
    }

    //update audio detection level for automatic silence detection
    fun updateAutomaticSilenceDetectionAudioLevel(audioLevel: String) {
        audioLevel.replace("""[-,. ]""".toRegex(), "")
            .toIntOrNull()?.also {
                AppSettings.automaticSilenceDetectionAudioLevel.value = it
            }
    }

    //toggle test (start or stop)
    fun toggleAudioLevelTest() {
        if (isRecording.value) {
            stopAudioLevelTest()
        } else {
            startAudioLevelTest()
        }
    }

    //stop recording when composable is not shown anymore
    fun onPause() {
        stopAudioLevelTest()
    }

    /**
     * start the audio level test will launch a job that listens to the audio recorder
     */
    private fun startAudioLevelTest() {
        _currentAudioLevel.value = 0
        _isRecording.value = true

        job?.cancel()
        job = CoroutineScope(Dispatchers.Default).launch {
            AudioRecorder.output.collectIndexed { _, value ->
                var max: Byte = 0
                value.forEach {
                    if (it >= max || it <= -max) {
                        max = it
                    }
                }
                _currentAudioLevel.value = max
            }
        }

        AudioRecorder.startRecording()
    }

    /**
     * stop audio level test stops recording and removes the job
     */
    private fun stopAudioLevelTest() {
        AudioRecorder.stopRecording()
        job?.cancel()
        job = null

        _isRecording.value = false
    }

}