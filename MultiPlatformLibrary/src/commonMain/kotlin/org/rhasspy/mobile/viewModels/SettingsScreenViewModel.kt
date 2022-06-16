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
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundFile

class SettingsScreenViewModel : ViewModel() {
    private val logger = Logger.withTag("SettingsScreenViewModel")

    private val currentAudioLevel = MutableLiveData<Byte>(0)
    val audioLevel: LiveData<Int> = currentAudioLevel.map { it.toInt() }

    private val currentStatus = MutableLiveData(false)
    val status: LiveData<Boolean> = currentStatus.readOnly()

    private val customSoundValues = MutableLiveData(AppSettings.customSounds.value.map { SoundFile(it, false) }.toTypedArray())
    val customSoundValuesUi: LiveData<Array<SoundFile>> = customSoundValues.readOnly()

    private var job: Job? = null

    init {
        checkCustomSoundUsage()
    }


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


    fun selectWakeSoundFile(fileName: String) {
        AppSettings.wakeSound.value = fileName
        checkCustomSoundUsage()
    }

    fun selectRecordedSoundFile(fileName: String) {
        AppSettings.recordedSound.value = fileName
        checkCustomSoundUsage()
    }

    fun selectErrorSoundFile(fileName: String) {
        AppSettings.errorSound.value = fileName
        checkCustomSoundUsage()
    }

    fun selectCustomSoundFile() = SettingsUtils.selectSoundFile { fileName ->
        fileName?.also {
            AppSettings.customSounds.value = AppSettings.customSounds.value.toMutableList()
                .apply {
                    this.add(it)
                }.toSet()

            customSoundValues.value = customSoundValues.value.toMutableList().apply {
                this.add(SoundFile(it, false))
            }.toTypedArray()
        }
    }

    fun removeCustomSoundFile(it: Int) {
        SettingsUtils.removeSoundFile(AppSettings.customSounds.value.elementAt(it))
        AppSettings.customSounds.value = AppSettings.customSounds.value.toMutableList()
            .apply {
                this.removeAt(it)
            }.toSet()
        checkCustomSoundUsage()
    }

    private fun soundFileIsUsed(fileName: String): Boolean =
        AppSettings.wakeSound.value == fileName || AppSettings.recordedSound.value == fileName || AppSettings.errorSound.value == fileName


    private fun checkCustomSoundUsage() {
        customSoundValues.value = AppSettings.customSounds.value.map { SoundFile(it, soundFileIsUsed(it)) }.toTypedArray()
    }

}