package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.*
import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.nativeutils.openLink
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword

class WakeWordConfigurationViewModel : ViewModel() {

    init {
        //TODO check files not found
    }

    //unsaved data
    private val _wakeWordOption = MutableStateFlow(ConfigurationSettings.wakeWordOption.value)
    private val _wakeWordPorcupineAccessToken = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineAccessToken.value)
    private val _wakeWordPorcupineKeywordDefaultOptions = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.value)
    private val _wakeWordPorcupineKeywordCustomOptions = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.value)
    private val _wakeWordPorcupineLanguage = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineLanguage.value)
    private val _wakeWordPorcupineSensitivity = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.value)

    //unsaved ui data
    val wakeWordOption = _wakeWordOption.readOnly
    val wakeWordPorcupineAccessToken = _wakeWordPorcupineAccessToken.readOnly
    val wakeWordPorcupineKeywordCount =
        combineState(_wakeWordPorcupineKeywordDefaultOptions, _wakeWordPorcupineKeywordCustomOptions) { default, custom ->
            default.size + custom.size
        }
    val wakeWordPorcupineKeywordDefaultOptions = _wakeWordPorcupineKeywordDefaultOptions.readOnly
    val wakeWordPorcupineKeywordCustomOptions = _wakeWordPorcupineKeywordCustomOptions.readOnly
    val wakeWordPorcupineLanguage = _wakeWordPorcupineLanguage.readOnly
    val wakeWordPorcupineSettingsVisible = _wakeWordOption.mapReadonlyState { it == WakeWordOption.Porcupine }

    val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_wakeWordOption, ConfigurationSettings.wakeWordOption.data),
        combineStateNotEquals(_wakeWordPorcupineAccessToken, ConfigurationSettings.wakeWordPorcupineAccessToken.data),
        combineStateNotEquals(_wakeWordPorcupineKeywordDefaultOptions, ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.data),
        combineStateNotEquals(_wakeWordPorcupineKeywordCustomOptions, ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.data),
        combineStateNotEquals(_wakeWordPorcupineLanguage, ConfigurationSettings.wakeWordPorcupineLanguage.data),
        combineStateNotEquals(_wakeWordPorcupineSensitivity, ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.data)
    )

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
    fun selectWakeWordPorcupineLanguage(option: PorcupineLanguageOptions) {
        _wakeWordPorcupineLanguage.value = option
    }

    //update porcupine wake word sensitivity
    fun updateWakeWordPorcupineKeywordDefaultSensitivity(index: Int, sensitivity: Float) {
        val newList = _wakeWordPorcupineKeywordDefaultOptions.value.toMutableList()
        newList[index] = _wakeWordPorcupineKeywordDefaultOptions.value.toList()[index].copy(sensitivity = sensitivity)
        _wakeWordPorcupineKeywordDefaultOptions.value = newList.toSet()
    }

    //predefined keywords (keyword, enabled)
    //custom keywords (keyword, enabled, can be deleted)

    fun clickPorcupineKeywordDefault(index: Int) {
        val newList = _wakeWordPorcupineKeywordDefaultOptions.value.toMutableList()
        newList[index] = _wakeWordPorcupineKeywordDefaultOptions.value.toList()[index].let {
            it.copy(enabled = !it.enabled)
        }
        _wakeWordPorcupineKeywordDefaultOptions.value = newList.toSet()
    }

    fun togglePorcupineKeywordDefault(index: Int, enabled: Boolean) {
        val newList = _wakeWordPorcupineKeywordDefaultOptions.value.toMutableList()
        newList[index] = _wakeWordPorcupineKeywordDefaultOptions.value.toList()[index].copy(enabled = enabled)
        _wakeWordPorcupineKeywordDefaultOptions.value = newList.toSet()
    }


    fun updateWakeWordPorcupineKeywordCustomSensitivity(index: Int, sensitivity: Float) {
        val newList = _wakeWordPorcupineKeywordCustomOptions.value.toMutableList()
        newList[index] = _wakeWordPorcupineKeywordCustomOptions.value.toList()[index].copy(sensitivity = sensitivity)
        _wakeWordPorcupineKeywordCustomOptions.value = newList.toSet()
    }

    fun clickPorcupineKeywordCustom(index: Int) {
        val newList = _wakeWordPorcupineKeywordCustomOptions.value.toMutableList()
        newList[index] = _wakeWordPorcupineKeywordCustomOptions.value.toList()[index].let {
            it.copy(enabled = !it.enabled)
        }
        _wakeWordPorcupineKeywordCustomOptions.value = newList.toSet()
    }

    fun togglePorcupineKeywordCustom(index: Int, enabled: Boolean) {
        val newList = _wakeWordPorcupineKeywordCustomOptions.value.toMutableList()
        newList[index] = _wakeWordPorcupineKeywordCustomOptions.value.toList()[index].copy(enabled = enabled)
        _wakeWordPorcupineKeywordCustomOptions.value = newList.toSet()
    }

    private val filesToDelete = mutableListOf<String>()
    private val newFiles = mutableListOf<String>()

    /**
     * add a custom keyword
     */
    fun addCustomPorcupineKeyword() {
        SettingsUtils.selectPorcupineFile { fileName ->
            fileName?.also {
                val newList = _wakeWordPorcupineKeywordCustomOptions.value.toMutableList()
                newList.add(PorcupineCustomKeyword(it, true, 0.5f))
                _wakeWordPorcupineKeywordCustomOptions.value = newList.toSet()
                newFiles.add(fileName)
            }
        }
    }

    fun downloadCustomPorcupineKeyword() {
        openLink("https://console.picovoice.ai/ppn")
    }

    /**
     * delete a custom keyword
     */
    fun deletePorcupineKeywordCustom(index: Int) {
        val newList = _wakeWordPorcupineKeywordCustomOptions.value.toMutableList()
        val fileName = newList[index].fileName
        newList.removeAt(index)
        _wakeWordPorcupineKeywordCustomOptions.value = newList.toSet()
        filesToDelete.add(fileName)
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.wakeWordOption.value = _wakeWordOption.value
        ConfigurationSettings.wakeWordPorcupineAccessToken.value = _wakeWordPorcupineAccessToken.value
        ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.value = _wakeWordPorcupineKeywordDefaultOptions.value
        ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
        ConfigurationSettings.wakeWordPorcupineLanguage.value = _wakeWordPorcupineLanguage.value
        ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.value = _wakeWordPorcupineSensitivity.value

        filesToDelete.forEach {
            //delte
        }
        filesToDelete.clear()
        newFiles.clear()
    }

    fun discard() {
        _wakeWordOption.value = ConfigurationSettings.wakeWordOption.value
        _wakeWordPorcupineAccessToken.value = ConfigurationSettings.wakeWordPorcupineAccessToken.value
        _wakeWordPorcupineKeywordDefaultOptions.value = ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.value
        _wakeWordPorcupineKeywordCustomOptions.value = ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.value
        _wakeWordPorcupineLanguage.value = ConfigurationSettings.wakeWordPorcupineLanguage.value
        _wakeWordPorcupineSensitivity.value = ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.value

        newFiles.forEach {
            //delte
        }
        filesToDelete.clear()
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

}