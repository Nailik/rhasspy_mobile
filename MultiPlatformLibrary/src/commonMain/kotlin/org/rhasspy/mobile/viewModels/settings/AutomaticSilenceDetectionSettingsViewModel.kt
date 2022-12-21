package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSettings
import kotlin.math.log
import kotlin.math.pow

class AutomaticSilenceDetectionSettingsViewModel : ViewModel() {

    //unsaved data
    private val _automaticSilenceDetectionTimeText =
        MutableStateFlow(AppSettings.automaticSilenceDetectionTime.value.toString())
    private val maxAudioLevel = AudioRecorder.absoluteMaxVolume

    //unsaved ui data
    val isAutomaticSilenceDetectionEnabled = AppSettings.isAutomaticSilenceDetectionEnabled.data

    val isSilenceDetectionSettingsVisible = isAutomaticSilenceDetectionEnabled
    val automaticSilenceDetectionTimeText = _automaticSilenceDetectionTimeText.readOnly

    val automaticSilenceDetectionAudioLevelPercentage =
        AppSettings.automaticSilenceDetectionAudioLevel.data.mapReadonlyState {
            (log(it.toDouble(), maxAudioLevel)).toFloat()
        }

    //testing
    val isRecording = AudioRecorder.isRecording
    val isSilenceDetectionAudioLevelVisible = isRecording
    val automaticSilenceDetectionAudioLevel = AppSettings.automaticSilenceDetectionAudioLevel.data
    val currentAudioLevel = AudioRecorder.maxVolume
    val isAudioLevelBiggerThanMax = combineState(
        AudioRecorder.maxVolume,
        AppSettings.automaticSilenceDetectionAudioLevel.data
    ) { audioLevel, max ->
        audioLevel > max
    }
    val audioLevelPercentage = currentAudioLevel.mapReadonlyState {
        (log(it.toDouble(), maxAudioLevel)).toFloat()
    }

    //set new intent recognition option
    fun toggleAutomaticSilenceDetectionEnabled(enabled: Boolean) {
        AppSettings.isAutomaticSilenceDetectionEnabled.value = enabled
    }

    //update time for automatic silence detection
    fun updateAutomaticSilenceDetectionTime(time: String) {
        val text = time.replace("""[-,. ]""".toRegex(), "")
        _automaticSilenceDetectionTimeText.value = text
        AppSettings.automaticSilenceDetectionTime.value = text.toIntOrNull() ?: 0
    }

    //update audio detection level for automatic silence detection
    fun changeAutomaticSilenceDetectionAudioLevelPercentage(value: Float) {
        AppSettings.automaticSilenceDetectionAudioLevel.value =
            maxAudioLevel.pow(value.toDouble()).toFloat()
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
        AudioRecorder.startRecording()
    }

    /**
     * stop audio level test stops recording and removes the job
     */
    private fun stopAudioLevelTest() {
        AudioRecorder.stopRecording()
    }

}