package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.nativeutils.BatteryOptimization
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundFile

class SettingsScreenViewModel : ViewModel() {
    private val logger = Logger.withTag("SettingsScreenViewModel")


    val currentLanguage: StateFlow<LanguageOptions> get() = AppSettings.languageOption.data
    val languageOptions = LanguageOptions::values

    fun selectLanguage(languageOption: LanguageOptions) {
        AppSettings.languageOption.value = languageOption
    }


    val currentTheme: StateFlow<ThemeOptions> get() = AppSettings.themeOption.data
    val themeOptions = ThemeOptions::values

    fun selectTheme(themeOption: ThemeOptions) {
        AppSettings.themeOption.value = themeOption
    }


    private val currentAudioLevel = MutableStateFlow<Byte>(0)
    val audioLevel: StateFlow<Int> = currentAudioLevel.mapReadonlyState { it.toInt() }

    private val currentStatus = MutableStateFlow(false)
    val status: StateFlow<Boolean> get() = currentStatus

    private val customSoundValues = MutableStateFlow(AppSettings.customSounds.value.map { SoundFile(it, false) }.toTypedArray())
    val customSoundValuesUi: StateFlow<Array<SoundFile>> get() = customSoundValues

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

    fun onDisableBatteryOptimization() {
        BatteryOptimization.openOptimizationSettings()
    }
    fun isBatteryOptimizationDisabled(): Boolean = BatteryOptimization.isBatteryOptimizationDisabled()

}
/**
 *

 */