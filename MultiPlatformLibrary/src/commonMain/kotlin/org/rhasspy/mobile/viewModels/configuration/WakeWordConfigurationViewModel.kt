package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.*
import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.nativeutils.openLink
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword

class WakeWordConfigurationViewModel : IConfigurationViewModel() {

    data class PorcupineCustomKeywordUi(
        val keyword: PorcupineCustomKeyword,
        val deleted: Boolean = false
    )

    //unsaved data
    private val _wakeWordOption = MutableStateFlow(ConfigurationSettings.wakeWordOption.value)
    private val _wakeWordPorcupineAccessToken = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineAccessToken.value)
    private val _wakeWordPorcupineKeywordDefaultOptions = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.value)
    private val _wakeWordPorcupineKeywordCustomOptions = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.value
        .map { PorcupineCustomKeywordUi(it) })
    private val _wakeWordPorcupineKeywordCustomOptionsNormal = _wakeWordPorcupineKeywordCustomOptions.mapReadonlyState { options ->
        options.map { it.keyword }.toSet()
    }

    private val _wakeWordPorcupineLanguage = MutableStateFlow(ConfigurationSettings.wakeWordPorcupineLanguage.value)

    //unsaved ui data
    val wakeWordOption = _wakeWordOption.readOnly
    val wakeWordPorcupineAccessToken = _wakeWordPorcupineAccessToken.readOnly
    val wakeWordPorcupineKeywordCount =
        combineState(_wakeWordPorcupineKeywordDefaultOptions, _wakeWordPorcupineKeywordCustomOptions) { default, custom ->
            default.filter { it.isEnabled }.size + custom.filter { it.keyword.isEnabled && !it.deleted }.size
        }
    val wakeWordPorcupineKeywordDefaultOptions = _wakeWordPorcupineKeywordDefaultOptions.readOnly
    val wakeWordPorcupineKeywordCustomOptions = _wakeWordPorcupineKeywordCustomOptions.readOnly
    val wakeWordPorcupineLanguage = _wakeWordPorcupineLanguage.readOnly
    fun isWakeWordPorcupineSettingsVisible(option: WakeWordOption): Boolean {
        return option == WakeWordOption.Porcupine
    }

    override val isTestingEnabled = _wakeWordOption.mapReadonlyState { it != WakeWordOption.Disabled }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_wakeWordOption, ConfigurationSettings.wakeWordOption.data),
        combineStateNotEquals(_wakeWordPorcupineAccessToken, ConfigurationSettings.wakeWordPorcupineAccessToken.data),
        combineStateNotEquals(_wakeWordPorcupineKeywordDefaultOptions, ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.data),
        combineStateNotEquals(_wakeWordPorcupineKeywordCustomOptionsNormal, ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.data),
        combineStateNotEquals(_wakeWordPorcupineLanguage, ConfigurationSettings.wakeWordPorcupineLanguage.data),
    )

    //for custom wake word
    private val newFiles = mutableListOf<String>()
    private val filesToDelete = mutableListOf<String>()

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
                it[index] = it[index].copy(isEnabled = !it[index].isEnabled)
            }.toSet()
    }

    //toggle default keyword
    fun togglePorcupineKeywordDefault(index: Int, enabled: Boolean) {
        _wakeWordPorcupineKeywordDefaultOptions.value = _wakeWordPorcupineKeywordDefaultOptions.value
            .toMutableList()
            .also {
                it[index] = it[index].copy(isEnabled = enabled)
            }.toSet()
    }

    //update custom keyword sensitivity
    fun updateWakeWordPorcupineKeywordCustomSensitivity(index: Int, sensitivity: Float) {
        _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .toMutableList()
            .also {
                it[index] = it[index].copy(keyword = it[index].keyword.copy(sensitivity = sensitivity))
            }
    }

    //enable/disable custom keyword
    fun clickPorcupineKeywordCustom(index: Int) {
        _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .toMutableList()
            .also {
                it[index] = it[index].copy(keyword = it[index].keyword.copy(isEnabled = !it[index].keyword.isEnabled))
            }
    }

    //toggle custom keyword
    fun togglePorcupineKeywordCustom(index: Int, enabled: Boolean) {
        _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .toMutableList()
            .also {
                it[index] = it[index].copy(keyword = it[index].keyword.copy(isEnabled = enabled))
            }
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
                        it.add(PorcupineCustomKeywordUi(PorcupineCustomKeyword(file, true, 0.5f)))
                        newFiles.add(file)
                    }
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
            .also {
                filesToDelete.add(it[index].keyword.fileName)
                it[index] = it[index].copy(deleted = true)
            }
    }

    /**
     * remove custom wake word from delete list
     */
    fun undoWakeWordPorcupineCustomKeywordDeleted(index: Int) {
        _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .toMutableList()
            .also {
                filesToDelete.remove(it[index].keyword.fileName)
                it[index] = it[index].copy(deleted = false)
            }
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSettings.wakeWordOption.value = _wakeWordOption.value
        ConfigurationSettings.wakeWordPorcupineAccessToken.value = _wakeWordPorcupineAccessToken.value
        ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.value = _wakeWordPorcupineKeywordDefaultOptions.value
        ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .filter { !it.deleted }.map { it.keyword }.toSet()
        ConfigurationSettings.wakeWordPorcupineLanguage.value = _wakeWordPorcupineLanguage.value

        filesToDelete.forEach {
            SettingsUtils.removePorcupineFile(it)
        }
        filesToDelete.clear()
        newFiles.clear()
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _wakeWordOption.value = ConfigurationSettings.wakeWordOption.value
        _wakeWordPorcupineAccessToken.value = ConfigurationSettings.wakeWordPorcupineAccessToken.value
        _wakeWordPorcupineKeywordDefaultOptions.value = ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.value
        _wakeWordPorcupineKeywordCustomOptions.value = ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.value
            .map { PorcupineCustomKeywordUi(it) }
        _wakeWordPorcupineLanguage.value = ConfigurationSettings.wakeWordPorcupineLanguage.value

        newFiles.forEach {
            SettingsUtils.removePorcupineFile(it)
        }
        filesToDelete.clear()
    }

    /**
     * test unsaved data configuration
     * also test if porcupine activation works
     */
    override fun onTest() {
        //TODO require microphone permission
        //TODO only when enabled
        //record audio
        //show when wakeword was detected, also which wakeword
        TODO()
    }

}