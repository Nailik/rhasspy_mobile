package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.data.PorcupineKeywordOptions
import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.nativeutils.openLink
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.settings.Setting
import org.rhasspy.mobile.settings.SettingsEnum

class WakeWordConfigurationViewModel : ViewModel() {

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
    val wakeWordPorcupineKeywordDefaultOptions = _wakeWordPorcupineKeywordDefaultOptions.readOnly
    val wakeWordPorcupineKeywordCustomOptions =  _wakeWordPorcupineKeywordCustomOptions.readOnly
    val wakeWordPorcupineLanguage = _wakeWordPorcupineLanguage.readOnly
    val wakeWordPorcupineSensitivity = _wakeWordPorcupineSensitivity.readOnly
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
    fun updateWakeWordPorcupineSensitivity(index: Int, sensitivity: Float) {//TODO replace?
        _wakeWordPorcupineKeywordDefaultOptions.value = _wakeWordPorcupineKeywordDefaultOptions.value.mapIndexed { i, triple ->
            if(i == index){
                triple.copy(third = sensitivity)
            } else {
                triple
            }
        }.toSet()
    }

    //predefined keywords (keyword, enabled)
    //custom keywords (keyword, enabled, can be deleted)

    fun clickPredefinedPorcupineKeyword(index: Int){
        _wakeWordPorcupineKeywordDefaultOptions.value = _wakeWordPorcupineKeywordDefaultOptions.value.mapIndexed { i, triple ->
            if(i == index){
                triple.copy(second = !triple.second)
            } else {
                triple
            }
        }.toSet()
    }
    fun togglePredefinedPorcupineKeyword(index: Int, enabled: Boolean){
        _wakeWordPorcupineKeywordDefaultOptions.value = _wakeWordPorcupineKeywordDefaultOptions.value.mapIndexed { i, triple ->
            if(i == index){
                triple.copy(second = enabled)
            } else {
                triple
            }
        }.toSet()
    }

    fun toggleCustomPorcupineKeyword(index: Int, enabled: Boolean){

    }

    fun deleteCustomPorcupineKeyword(index: Int){

    }


    fun predefinedKeywordSensitivity(index: Int) : StateFlow<Float> {
        return MutableStateFlow(0f)
    }
    fun customKeywordSensitivity(index: Int) : StateFlow<Float> {
        return MutableStateFlow(0f)
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
    }

    fun discard() {
        _wakeWordOption.value = ConfigurationSettings.wakeWordOption.value
        _wakeWordPorcupineAccessToken.value = ConfigurationSettings.wakeWordPorcupineAccessToken.value
        _wakeWordPorcupineKeywordDefaultOptions.value = ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.value
        _wakeWordPorcupineKeywordCustomOptions.value = ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.value
        _wakeWordPorcupineLanguage.value = ConfigurationSettings.wakeWordPorcupineLanguage.value
        _wakeWordPorcupineSensitivity.value = ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.value
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
       /* fileName?.also {
            _wakeWordPorcupineKeywordOptions.value = _wakeWordPorcupineKeywordOptions.value.toMutableList()
                .apply {
                    this.add(it)
                }.toSet()
            _wakeWordPorcupineKeywordOption.value = _wakeWordPorcupineKeywordOptions.value.size - 1
        }*/
    }

}