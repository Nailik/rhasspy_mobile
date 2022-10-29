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

    //unsaved data
    private val _wakeWordOption = MutableStateFlow(ConfigurationSettings.wakeWordOption.value)
    private val _wakeWordPorcupineAccessToken = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineAccessToken.value)
    private val _wakeWordPorcupineKeywordDefaultOptions = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.value)
    private val _wakeWordPorcupineKeywordCustomOptions = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.value)
    private val _wakeWordPorcupineKeywordCustomOptionsRemoved = MutableStateFlow(listOf<PorcupineCustomKeyword>())

    private val _wakeWordPorcupineLanguage = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineLanguage.value)
    private val _wakeWordPorcupineSensitivity = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.value)

    //unsaved ui data
    val wakeWordOption = _wakeWordOption.readOnly
    val wakeWordPorcupineAccessToken = _wakeWordPorcupineAccessToken.readOnly
    val wakeWordPorcupineKeywordCount =
        combineState(_wakeWordPorcupineKeywordDefaultOptions, _wakeWordPorcupineKeywordCustomOptions) { default, custom ->
            default.filter { it.enabled }.size + custom.filter { it.enabled }.size
        }
    val wakeWordPorcupineKeywordDefaultOptions = _wakeWordPorcupineKeywordDefaultOptions.readOnly
    val wakeWordPorcupineKeywordCustomOptions = _wakeWordPorcupineKeywordCustomOptions.readOnly
    val wakeWordPorcupineKeywordCustomOptionsRemoved = _wakeWordPorcupineKeywordCustomOptionsRemoved.readOnly
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

    //for custom wake word
    private val filesToDelete = mutableListOf<String>()
    private val newFiles = mutableListOf<String>()

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
        _wakeWordPorcupineKeywordDefaultOptions.value = _wakeWordPorcupineKeywordDefaultOptions.value
            .toMutableList()
            .also {
                it[index] = it[index].copy(sensitivity = sensitivity)
            }.toSet()
    }

    //enable/disable default keyword
    fun clickPorcupineKeywordDefault(index: Int) {
        _wakeWordPorcupineKeywordDefaultOptions.value = _wakeWordPorcupineKeywordDefaultOptions.value
            .toMutableList()
            .also {
                it[index] = it[index].copy(enabled = !it[index].enabled)
            }.toSet()
    }

    //toggle default keyword
    fun togglePorcupineKeywordDefault(index: Int, enabled: Boolean) {
        _wakeWordPorcupineKeywordDefaultOptions.value = _wakeWordPorcupineKeywordDefaultOptions.value
            .toMutableList()
            .also {
                it[index] = it[index].copy(enabled = enabled)
            }.toSet()
    }

    //update custom keyword sensitivity
    fun updateWakeWordPorcupineKeywordCustomSensitivity(index: Int, sensitivity: Float) {
        _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .toMutableList()
            .also {
                it[index] = it[index].copy(sensitivity = sensitivity)
            }.toSet()
    }

    //enable/disable custom keyword
    fun clickPorcupineKeywordCustom(index: Int) {
        _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .toMutableList()
            .also {
                it[index] = it[index].copy(enabled = !it[index].enabled)
            }.toSet()
    }

    //toggle custom keyword
    fun togglePorcupineKeywordCustom(index: Int, enabled: Boolean) {
        _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .toMutableList()
            .also {
                it[index] = it[index].copy(enabled = enabled)
            }.toSet()
    }


    /**
     * add a custom keyword
     */
    fun addCustomPorcupineKeyword() {
        SettingsUtils.selectPorcupineFile { fileName ->
            fileName?.also { file ->
                _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
                    .toMutableList()
                    .also {
                        it.add(PorcupineCustomKeyword(file, true, 0.5f))
                        newFiles.add(file)
                    }.toSet()
            }
        }
    }

    /**
     * open link where user can download keyword
     */
    fun downloadCustomPorcupineKeyword() {
        openLink("https://console.picovoice.ai/ppn")
    }

    /**
     * open picovoice console to create access token
     */
    fun openPicoVoiceConsole() {
        openLink("https://console.picovoice.ai")
    }

    /**
     * delete a custom keyword
     */
    fun deletePorcupineKeywordCustom(index: Int) {
        _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .toMutableList()
            .also { keywordList ->
                val item = keywordList[index]
                keywordList.removeAt(index)
                filesToDelete.add(item.fileName)

                _wakeWordPorcupineKeywordCustomOptionsRemoved.value = _wakeWordPorcupineKeywordCustomOptionsRemoved.value
                    .toMutableList()
                    .also {
                        it.add(item)
                    }
            }.toSet()
    }

    /**
     * remove custom wakeword from delete list
     */
    fun undoWakeWordPorcupineCustomKeywordDeleted(index: Int) {
        _wakeWordPorcupineKeywordCustomOptionsRemoved.value = _wakeWordPorcupineKeywordCustomOptionsRemoved.value
            .toMutableList()
            .also { deletedKeywordList ->
                val item = deletedKeywordList[index]
                deletedKeywordList.removeAt(index)

                _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
                    .toMutableList()
                    .also {
                        it.add(item)
                    }.toSet()
            }
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
            SettingsUtils.removePorcupineFile(it)
        }
        filesToDelete.clear()
        newFiles.clear()
        _wakeWordPorcupineKeywordCustomOptionsRemoved.value = emptyList()
    }

    /**
     * undo all changes
     */
    fun discard() {
        _wakeWordOption.value = ConfigurationSettings.wakeWordOption.value
        _wakeWordPorcupineAccessToken.value = ConfigurationSettings.wakeWordPorcupineAccessToken.value
        _wakeWordPorcupineKeywordDefaultOptions.value = ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.value
        _wakeWordPorcupineKeywordCustomOptions.value = ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.value
        _wakeWordPorcupineLanguage.value = ConfigurationSettings.wakeWordPorcupineLanguage.value
        _wakeWordPorcupineSensitivity.value = ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.value

        newFiles.forEach {
            SettingsUtils.removePorcupineFile(it)
        }
        filesToDelete.clear()
        _wakeWordPorcupineKeywordCustomOptionsRemoved.value = emptyList()
    }

    /**
     * test unsaved data configuration
     * also test if porcupine activation works
     */
    fun test() {

    }

}