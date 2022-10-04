package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.logger.LogLevel
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.nativeutils.BatteryOptimization
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundFile

class SettingsScreenViewModel : ViewModel() {
    private val logger = Logger.withTag("SettingsScreenViewModel")

    private val customSoundValues = MutableStateFlow(AppSettings.customSounds.value.map { SoundFile(it, false) }.toTypedArray())
    init {
        checkCustomSoundUsage()
    }

    //Language item
    val currentLanguage: StateFlow<LanguageOptions> get() = AppSettings.languageOption.data
    val languageOptions = LanguageOptions::values

    fun selectLanguage(languageOption: LanguageOptions) {
        AppSettings.languageOption.value = languageOption
    }

    //theme item
    val currentTheme: StateFlow<ThemeOptions> get() = AppSettings.themeOption.data
    val themeOptions = ThemeOptions::values

    fun selectTheme(themeOption: ThemeOptions) {
        AppSettings.themeOption.value = themeOption
    }

    //background service item
    val isBackgroundEnabled: StateFlow<Boolean> get() = AppSettings.isBackgroundEnabled.data

    fun toggleBackgroundEnabled(value: Boolean) {
        AppSettings.isBackgroundEnabled.value = value
    }

    fun isBatteryOptimizationDisabled(): Boolean = BatteryOptimization.isBatteryOptimizationDisabled()

    fun onDisableBatteryOptimization() {
        BatteryOptimization.openOptimizationSettings()
    }

    //microphone overlay
    val isMicrophoneOverlayEnabled: StateFlow<Boolean> get() = AppSettings.isMicrophoneOverlayEnabled.data
    val isMicrophoneOverlayWhileApp: StateFlow<Boolean> get() = AppSettings.isMicrophoneOverlayWhileApp.data

    fun updateMicrophoneEnabled(value: Boolean) {
        if (value && !OverlayPermission.granted.value) {
            OverlayPermission.requestPermission {
                if (it) {
                    AppSettings.isMicrophoneOverlayEnabled.value = value
                }
            }
        } else {
            AppSettings.isMicrophoneOverlayEnabled.value = value
        }
    }

    fun updateMicrophoneOverlayWhileApp(value: Boolean) {
        AppSettings.isMicrophoneOverlayWhileApp.value = value
    }


    //WakeWordIndicationItem
    val isWakeWordSoundIndicationEnabled: StateFlow<Boolean> get() = AppSettings.isWakeWordSoundIndicationEnabled.data
    val isWakeWordLightIndicationEnabled: StateFlow<Boolean> get() = AppSettings.isWakeWordLightIndicationEnabled.data
    val isBackgroundWakeWordDetectionTurnOnDisplay: StateFlow<Boolean> get() = AppSettings.isBackgroundWakeWordDetectionTurnOnDisplayEnabled.data

    fun updateWakeWordSoundIndicationEnabled(value: Boolean) {
        AppSettings.isWakeWordSoundIndicationEnabled.value = value
    }

    fun updateWakeWordLightIndicationEnabled(value: Boolean) {
        AppSettings.isWakeWordLightIndicationEnabled.value = value
    }

    fun updateBackgroundWakeWordDetectionTurnOnDisplay(value: Boolean) {
        if (value && !OverlayPermission.granted.value) {
            OverlayPermission.requestPermission {
                if (it) {
                    AppSettings.isBackgroundWakeWordDetectionTurnOnDisplayEnabled.value = value
                }
            }
        } else {
            AppSettings.isBackgroundWakeWordDetectionTurnOnDisplayEnabled.value = value
        }
    }

    //sounds
    val soundVolume: StateFlow<Float> get() = AppSettings.soundVolume.data

    val customSounds: StateFlow<Set<String>> get() = AppSettings.customSounds.data

    val wakeSound: StateFlow<String> get() = AppSettings.wakeSound.data
    val recordedSound: StateFlow<String> get() = AppSettings.recordedSound.data
    val errorSound: StateFlow<String> get() = AppSettings.errorSound.data

    val customSoundValuesUi: StateFlow<Array<SoundFile>> get() = customSoundValues

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

    fun updateSoundVolume(value: Float) {
        AppSettings.soundVolume.value = value
    }

    //device
    val volume: StateFlow<Float> get() = AppSettings.volume.data

    fun updateVolume(value: Float) {
        AppSettings.volume.value = value
    }

    val isHotWordEnabled: StateFlow<Boolean> get() = AppSettings.isHotWordEnabled.data
    val isAudioOutputEnabled: StateFlow<Boolean> get() = AppSettings.isAudioOutputEnabled.data
    val isIntentHandlingEnabled: StateFlow<Boolean> get() = AppSettings.isIntentHandlingEnabled.data

    fun updateHotWordEnabled(value: Boolean) {
        AppSettings.isHotWordEnabled.value = value
    }

    fun updateAudioOutputEnabled(value: Boolean) {
        AppSettings.isAudioOutputEnabled.value = value
    }

    fun updateIntentHandlingEnabled(value: Boolean) {
        AppSettings.isIntentHandlingEnabled.value = value
    }

    //audio Level
    val isAutomaticSilenceDetectionEnabled: StateFlow<Boolean> get() = AppSettings.isAutomaticSilenceDetectionEnabled.data
    val automaticSilenceDetectionTime: StateFlow<Int> get() = AppSettings.automaticSilenceDetectionTime.data
    val automaticSilenceDetectionAudioLevel: StateFlow<Int> get() = AppSettings.automaticSilenceDetectionAudioLevel.data

    fun updateAutomaticSilenceDetectionEnabled(value: Boolean) {
        AppSettings.isAutomaticSilenceDetectionEnabled.value = value
    }

    fun updateAutomaticSilenceDetectionTime(value: String) {
        value.replace("-", "")
            .replace(",", "")
            .replace(".", "")
            .replace(" ", "")
            .toIntOrNull()?.also {

                AppSettings.automaticSilenceDetectionTime.value = it
                logger.v { "parsed automaticSilenceDetectionTime to $it" }

            }
    }
    fun updateAutomaticSilenceDetectionAudioLevel(value: String) {
        value.replace("-", "")
            .replace(",", "")
            .replace(".", "")
            .replace(" ", "")
            .toIntOrNull()?.also {

                AppSettings.automaticSilenceDetectionAudioLevel.value = it
                logger.v { "parsed automaticSilenceDetectionAudioLevel to $it" }

            }
    }

    private val currentAudioLevel = MutableStateFlow<Byte>(0)
    val audioLevel: StateFlow<Int> = currentAudioLevel.mapReadonlyState { it.toInt() }

    private val currentStatus = MutableStateFlow(false)
    val status: StateFlow<Boolean> get() = currentStatus


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

    //log settings
    val logLevel: StateFlow<LogLevel> get() = AppSettings.logLevel.data
    val isShowLogEnabled: StateFlow<Boolean> get() = AppSettings.isShowLogEnabled.data
    val isLogAudioFramesEnabled: StateFlow<Boolean> get() = AppSettings.isLogAudioFramesEnabled.data

    fun updateLogLevel(value: LogLevel) {
        AppSettings.logLevel.value = value
    }
    fun updateShowLogEnabled(value: Boolean) {
        AppSettings.isShowLogEnabled.value = value
    }
    fun updateLogAudioFramesEnabled(value: Boolean) {
        AppSettings.isLogAudioFramesEnabled.value = value
    }

    //problem handling

    val isForceCancelEnabled: StateFlow<Boolean> get() = AppSettings.isForceCancelEnabled.data

    fun updateForceCancelEnabled(value: Boolean) {
        AppSettings.isForceCancelEnabled.value = value
    }



    //export and import settings

    fun saveSettingsFile() = SettingsUtils.saveSettingsFile()

    fun restoreSettingsFromFile() = SettingsUtils.restoreSettingsFromFile()


}
/**
 *

 */