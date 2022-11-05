package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundFile

class WakeWordIndicationSettingsViewModel : ViewModel() {

    //unsaved data
    private val _isWakeWordSoundIndicationEnabled = MutableStateFlow(AppSettings.isWakeWordSoundIndicationEnabled.value)
    private val _isWakeWordLightIndicationEnabled = MutableStateFlow(AppSettings.isWakeWordLightIndicationEnabled.value)
    private val _isWakeWordDetectionTurnOnDisplayEnabled = MutableStateFlow(AppSettings.isWakeWordDetectionTurnOnDisplayEnabled.value)
    private val _soundVolume = MutableStateFlow(AppSettings.soundVolume.value)
    private val _wakeSound = MutableStateFlow(AppSettings.wakeSound.value)
    private val _recordedSound = MutableStateFlow(AppSettings.recordedSound.value)
    private val _errorSound = MutableStateFlow(AppSettings.errorSound.value)
    private val _customSounds = MutableStateFlow(AppSettings.customSounds.value)
    private val _customSoundValues = MutableStateFlow(AppSettings.customSounds.value.map { SoundFile(it, false) }.toTypedArray())
    private val _removedFiles = mutableListOf<String>()

    //unsaved ui data
    val isWakeWordSoundIndicationEnabled = _isWakeWordSoundIndicationEnabled.readOnly
    val isWakeWordLightIndicationEnabled = _isWakeWordLightIndicationEnabled.readOnly
    val isWakeWordDetectionTurnOnDisplayEnabled = _isWakeWordDetectionTurnOnDisplayEnabled.readOnly
    val isSoundSettingsVisible = isWakeWordSoundIndicationEnabled
    val soundVolume = _soundVolume.readOnly
    val wakeSound = _wakeSound.readOnly
    val recordedSound = _recordedSound.readOnly
    val errorSound = _errorSound.readOnly
    val customSounds = _customSounds.readOnly
    val customSoundValues = _customSoundValues.readOnly

    init {
        //after initialization of variables
        checkCustomSoundUsage()
    }

    //toggle wake word sound indication
    fun toggleWakeWordSoundIndicationEnabled(enabled: Boolean) {
        _isWakeWordSoundIndicationEnabled.value = enabled
    }

    //toggle wake word light indication
    fun toggleWakeWordLightIndicationEnabled(enabled: Boolean) {
        _isWakeWordLightIndicationEnabled.value = enabled
    }

    //toggle wake word turn on display
    fun toggleWakeWordDetectionTurnOnDisplay(enabled: Boolean) {
        _isWakeWordDetectionTurnOnDisplayEnabled.value = enabled
    }

    //update sound volume
    fun updateSoundVolume(volume: Float) {
        _soundVolume.value = volume
    }

    //select sound file for wake sound
    fun selectWakeSoundFile(fileName: String) {
        _wakeSound.value = fileName
    }

    //select sound file for recorded sound
    fun selectRecordedSoundFile(fileName: String) {
        _recordedSound.value = fileName
    }

    //select sound file for error sound
    fun selectErrorSoundFile(fileName: String) {
        _errorSound.value = fileName
    }

    /**
     * add custom sound file
     */
    fun selectCustomSoundFile() = SettingsUtils.selectSoundFile { fileName ->
        fileName?.also {
            _customSounds.value = _customSounds.value.toMutableList()
                .apply {
                    this.add(it)
                }.toSet()

            _customSoundValues.value = _customSoundValues.value.toMutableList().apply {
                this.add(SoundFile(it, false))
            }.toTypedArray()
        }
    }

    /**
     * remove a custom sound file
     */
    fun removeCustomSoundFile(index: Int) {
        _customSounds.value = _customSounds.value.toMutableList()
            .apply {
                _removedFiles.add(this[index])
                this.removeAt(index)
            }.toSet()
        checkCustomSoundUsage()
    }

    /**
     * check if any sound file is used
     */
    private fun checkCustomSoundUsage() {
        _customSoundValues.value = _customSounds.value.map { SoundFile(it, soundFileIsUsed(it)) }.toTypedArray()
    }

    /**
     * check if a sound file is used
     */
    private fun soundFileIsUsed(fileName: String): Boolean =
        _wakeSound.value == fileName || _recordedSound.value == fileName || _errorSound.value == fileName

    /**
     * save data configuration
     */
    fun save() {
        AppSettings.isWakeWordSoundIndicationEnabled.value = _isWakeWordSoundIndicationEnabled.value
        AppSettings.isWakeWordLightIndicationEnabled.value = _isWakeWordLightIndicationEnabled.value
        AppSettings.isWakeWordDetectionTurnOnDisplayEnabled.value = _isWakeWordDetectionTurnOnDisplayEnabled.value
        AppSettings.soundVolume.value = _soundVolume.value
        AppSettings.wakeSound.value = _wakeSound.value
        AppSettings.recordedSound.value = _recordedSound.value
        AppSettings.errorSound.value = _errorSound.value
        AppSettings.customSounds.value = _customSounds.value

        //remove sound files that are remove in ui
        _removedFiles.forEach {
            SettingsUtils.removeSoundFile(it)
        }
        _removedFiles.clear()
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}