package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.nativeutils.openLink
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

class WakeWordConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _wakeWordOption = MutableStateFlow(ConfigurationSettings.wakeWordOption.value)
    private val _wakeWordPorcupineAccessToken = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineAccessToken.value)
    private val _wakeWordPorcupineKeywordOption = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineKeywordOption.value)
    private val _wakeWordPorcupineKeywordOptions = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineKeywordOptions.value)
    private val _wakeWordPorcupineLanguage = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineLanguage.value)
    private val _wakeWordPorcupineSensitivity = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.value)

    //unsaved ui data
    val wakeWordOption = _wakeWordOption.readOnly
    val wakeWordPorcupineAccessToken = _wakeWordPorcupineAccessToken.readOnly
    val wakeWordPorcupineKeywordOption = _wakeWordPorcupineKeywordOption.readOnly
    val wakeWordPorcupineKeywordOptions = _wakeWordPorcupineKeywordOptions.readOnly
    val wakeWordPorcupineLanguage = _wakeWordPorcupineLanguage.readOnly
    val wakeWordPorcupineSensitivity = _wakeWordPorcupineSensitivity.readOnly
    val wakeWordPorcupineSettingsVisible = _wakeWordOption.mapReadonlyState { it == WakeWordOption.Porcupine }

    //all options
    val wakeWordOptions = WakeWordOption::values
    val porcupineLanguageOptions = PorcupineLanguageOptions::values

    //select wake word option
    fun selectWakeWordOption(option: WakeWordOption) {
        _wakeWordOption.value = option
    }

    //set porcupine access token
    fun updateWakeWordPorcupineAccessToken(accessToken: String) {
        _wakeWordPorcupineAccessToken.value = accessToken
    }

    //set porcupine keyword option
    fun selectWakeWordPorcupineKeywordOption(option: Int) {
        _wakeWordPorcupineKeywordOption.value = option
    }

    //set porcupine keyword option
    fun selectWakeWordPorcupineLanguage(option: PorcupineLanguageOptions) {
        _wakeWordPorcupineLanguage.value = option
    }

    //update porcupine wake word sensitivity
    fun updateWakeWordPorcupineSensitivity(sensitivity: Float) {
        _wakeWordPorcupineSensitivity.value = sensitivity
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.wakeWordOption.data.value = _wakeWordOption.value
        ConfigurationSettings.wakeWordPorcupineAccessToken.data.value = _wakeWordPorcupineAccessToken.value
        ConfigurationSettings.wakeWordPorcupineKeywordOption.data.value = _wakeWordPorcupineKeywordOption.value
        ConfigurationSettings.wakeWordPorcupineKeywordOptions.data.value = _wakeWordPorcupineKeywordOptions.value
        ConfigurationSettings.wakeWordPorcupineLanguage.data.value = _wakeWordPorcupineLanguage.value
        ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.data.value = _wakeWordPorcupineSensitivity.value
    }

    /**
     * test unsaved data configuration
     * also test if porcupine activation works
     */
    fun test() {

    }

    /**
     * open picovoice console to create access token
     */
    fun openPicoVoiceConsole() {
        openLink("https://console.picovoice.ai")
    }

    /**
     * select a porcupine file from filemanager
     */
    fun selectPorcupineWakeWordFile() = SettingsUtils.selectPorcupineFile { fileName ->
        fileName?.also {
            _wakeWordPorcupineKeywordOptions.value = _wakeWordPorcupineKeywordOptions.value.toMutableList()
                .apply {
                    this.add(it)
                }.toSet()
            _wakeWordPorcupineKeywordOption.value = _wakeWordPorcupineKeywordOptions.value.size - 1
        }
    }

}