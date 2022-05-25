package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.map
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.settings.AppSettings

class SettingsScreenViewModel : ViewModel() {
    private val logger = Logger.withTag("SettingsScreenViewModel")

    private val currentAudioLevel = MutableLiveData<Byte>(0)
    val audioLevel: LiveData<Int> = currentAudioLevel.map { it.toInt() }

    private val currentStatus = MutableLiveData(false)
    val status: LiveData<Boolean> = currentStatus.readOnly()

    private var job: Job? = null


    fun toggleAudioLevelTest() {
        if (currentStatus.value) {
            stopAudioLevelTest()
        } else {
            startAudioLevelTest()
        }
    }

    private fun startAudioLevelTest() {
        logger.v { "startAudioLevelTest" }

        currentAudioLevel.value = 0
        currentStatus.value = true

        job = CoroutineScope(Dispatchers.Default).launch {
            AudioRecorder.output.collectIndexed { _, value ->
                var max: Byte = 0
                value.forEach {
                    if (it >= max || it <= -max) {
                        max = it
                    }
                }
                viewModelScope.launch {
                    currentAudioLevel.value = max
                }
            }
        }

        AudioRecorder.startRecording()
    }

    private fun stopAudioLevelTest() {
        logger.v { "stopAudioLevelTest" }

        AudioRecorder.stopRecording()
        job?.cancel()

        currentStatus.value = false
    }

    fun saveSettingsFile() = SettingsUtils.saveSettingsFile()

    fun restoreSettingsFromFile() = SettingsUtils.restoreSettingsFromFile()

    fun selectWakeSoundFile() = SettingsUtils.selectSoundFile { fileName ->
        fileName?.also {
            AppSettings.wakeSounds.data = AppSettings.wakeSounds.data.toMutableList()
                .apply {
                    this.add(it)
                }.toSet()
            AppSettings.wakeSound.data = AppSettings.wakeSounds.data.size + 2
        }
    }

    fun selectRecordedSoundFile() = SettingsUtils.selectSoundFile { fileName ->
        fileName?.also {
            AppSettings.recordedSounds.data = AppSettings.recordedSounds.data.toMutableList()
                .apply {
                    this.add(it)
                }.toSet()
            AppSettings.recordedSound.data = AppSettings.recordedSounds.data.size + 2
        }
    }

    fun selectErrorSoundFile() = SettingsUtils.selectSoundFile { fileName ->
        fileName?.also {
            AppSettings.errorSounds.data = AppSettings.errorSounds.data.toMutableList()
                .apply {
                    this.add(it)
                }.toSet()
            AppSettings.errorSound.data = AppSettings.errorSounds.data.size + 2
        }
    }


}