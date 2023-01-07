package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.*
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.nativeutils.FileUtils
import org.rhasspy.mobile.nativeutils.openLink
import org.rhasspy.mobile.services.udp.UdpServiceParams
import org.rhasspy.mobile.services.wakeword.WakeWordService
import org.rhasspy.mobile.services.wakeword.WakeWordServiceParams
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.PorcupineKeywordOption
import org.rhasspy.mobile.settings.option.PorcupineLanguageOption
import org.rhasspy.mobile.settings.option.WakeWordOption
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.settings.types.FileType
import org.rhasspy.mobile.viewmodel.configuration.test.WakeWordConfigurationTest

class WakeWordConfigurationViewModel : IConfigurationViewModel() {

    //use get to fix issues where instance dies because udp socket causes thread exception
    override val testRunner by inject<WakeWordConfigurationTest>()
    override val logType = LogType.WakeWordService
    override val serviceState get() = get<WakeWordService>().serviceState

    data class PorcupineCustomKeywordUi(
        val keyword: PorcupineCustomKeyword,
        val deleted: Boolean = false
    )

    //unsaved data
    private val _wakeWordOption = MutableStateFlow(ConfigurationSetting.wakeWordOption.value)
    private val _wakeWordPorcupineAccessToken = MutableStateFlow(ConfigurationSetting.wakeWordPorcupineAccessToken.value)
    private val _wakeWordPorcupineKeywordDefaultOptions = MutableStateFlow(ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value)
    private val _wakeWordPorcupineKeywordCustomOptions = MutableStateFlow(ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value
        .map { PorcupineCustomKeywordUi(it) })
    private val _wakeWordPorcupineKeywordCustomOptionsNormal = _wakeWordPorcupineKeywordCustomOptions
        .mapReadonlyState { options ->
            options.map { it.keyword }.toSet()
        }

    private val _wakeWordPorcupineLanguage = MutableStateFlow(ConfigurationSetting.wakeWordPorcupineLanguage.value)

    private val _udpOutputHost = MutableStateFlow(ConfigurationSetting.udpOutputHost.value)
    private val _udpOutputPort = MutableStateFlow(ConfigurationSetting.udpOutputPort.value)
    private val _udpOutputPortText = MutableStateFlow(ConfigurationSetting.udpOutputPort.value.toString())

    //unsaved ui data
    val wakeWordOption = _wakeWordOption.readOnly
    val wakeWordPorcupineAccessToken = _wakeWordPorcupineAccessToken.readOnly

    val wakeWordPorcupineKeywordCount = combineState(
        _wakeWordPorcupineKeywordDefaultOptions,
        _wakeWordPorcupineKeywordCustomOptions,
        _wakeWordPorcupineLanguage
    ) { default, custom, language ->
        default.filter { it.isEnabled && it.option.language == language }.size + custom.filter { it.keyword.isEnabled && !it.deleted }.size
    }

    val wakeWordPorcupineKeywordDefaultOptions = combineState(
        _wakeWordPorcupineKeywordDefaultOptions,
        _wakeWordPorcupineLanguage
    ) { options, language ->
        options.filter { it.option.language == language }
    }

    val wakeWordPorcupineKeywordCustomOptions = _wakeWordPorcupineKeywordCustomOptions.readOnly
    val wakeWordPorcupineLanguage = _wakeWordPorcupineLanguage.readOnly
    val udpOutputHost = _udpOutputHost.readOnly
    val udpOutputPortText = _udpOutputPortText.readOnly


    fun isWakeWordPorcupineSettingsVisible(option: WakeWordOption): Boolean {
        return option == WakeWordOption.Porcupine
    }

    fun isUdpOutputSettingsVisible(option: WakeWordOption): Boolean {
        return option == WakeWordOption.Udp
    }

    override val isTestingEnabled =
        _wakeWordOption.mapReadonlyState { it != WakeWordOption.Disabled }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_wakeWordOption, ConfigurationSetting.wakeWordOption.data),
        combineStateNotEquals(_wakeWordPorcupineAccessToken, ConfigurationSetting.wakeWordPorcupineAccessToken.data),
        combineStateNotEquals(_wakeWordPorcupineKeywordDefaultOptions, ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.data),
        combineStateNotEquals(_wakeWordPorcupineKeywordCustomOptionsNormal, ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.data),
        combineStateNotEquals(_wakeWordPorcupineLanguage, ConfigurationSetting.wakeWordPorcupineLanguage.data),
        combineStateNotEquals(_udpOutputHost, ConfigurationSetting.udpOutputHost.data),
        combineStateNotEquals(_udpOutputPort, ConfigurationSetting.udpOutputPort.data)
    )

    //for custom wake word
    private val newFiles = mutableListOf<String>()
    private val filesToDelete = mutableListOf<String>()

    //all options
    val wakeWordOptions = WakeWordOption::values
    val porcupineLanguageOption = PorcupineLanguageOption::values

    //select wake word option
    fun selectWakeWordOption(option: WakeWordOption) {
        _wakeWordOption.value = option
    }

    //set porcupine access token
    fun updateWakeWordPorcupineAccessToken(accessToken: String) {
        _wakeWordPorcupineAccessToken.value = accessToken
    }

    //set porcupine keyword option
    fun selectWakeWordPorcupineLanguage(option: PorcupineLanguageOption) {
        _wakeWordPorcupineLanguage.value = option
    }

    //update porcupine wake word sensitivity
    fun updateWakeWordPorcupineKeywordDefaultSensitivity(
        option: PorcupineKeywordOption,
        sensitivity: Float
    ) {
        _wakeWordPorcupineKeywordDefaultOptions.value =
            _wakeWordPorcupineKeywordDefaultOptions.value.let { list ->
                val index = list.indexOf(list.find { it.option == option })

                list.toMutableList()
                    .also { it[index] = it[index].copy(sensitivity = sensitivity) }
                    .toSet()
            }
    }

    //enable/disable default keyword
    fun clickPorcupineKeywordDefault(option: PorcupineKeywordOption) {
        _wakeWordPorcupineKeywordDefaultOptions.value =
            _wakeWordPorcupineKeywordDefaultOptions.value.let { list ->
                val index = list.indexOf(list.find { it.option == option })

                list.toMutableList()
                    .also { it[index] = it[index].copy(isEnabled = !it[index].isEnabled) }
                    .toSet()
            }
    }

    //toggle default keyword
    fun togglePorcupineKeywordDefault(option: PorcupineKeywordOption, enabled: Boolean) {
        _wakeWordPorcupineKeywordDefaultOptions.value =
            _wakeWordPorcupineKeywordDefaultOptions.value.let { list ->
                val index = list.indexOf(list.find { it.option == option })

                list.toMutableList()
                    .also { it[index] = it[index].copy(isEnabled = enabled) }
                    .toSet()
            }
    }

    //update custom keyword sensitivity
    fun updateWakeWordPorcupineKeywordCustomSensitivity(index: Int, sensitivity: Float) {
        _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .toMutableList()
            .also {
                it[index] =
                    it[index].copy(keyword = it[index].keyword.copy(sensitivity = sensitivity))
            }
    }

    //enable/disable custom keyword
    fun clickPorcupineKeywordCustom(index: Int) {
        _wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .toMutableList()
            .also {
                it[index] =
                    it[index].copy(keyword = it[index].keyword.copy(isEnabled = !it[index].keyword.isEnabled))
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
        viewModelScope.launch {
            FileUtils.selectFile(FileType.PORCUPINE)?.also { file ->
                _wakeWordPorcupineKeywordCustomOptions.value =
                    _wakeWordPorcupineKeywordCustomOptions.value
                        .toMutableList()
                        .also {
                            it.add(
                                PorcupineCustomKeywordUi(
                                    PorcupineCustomKeyword(
                                        file,
                                        true,
                                        0.5f
                                    )
                                )
                            )
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

    //edit udp port
    fun changeUdpOutputPort(port: String) {
        val text = port.replace("""[-,. ]""".toRegex(), "")
        _udpOutputPortText.value = text
        _udpOutputPort.value = text.toIntOrNull() ?: 0
    }

    //edit udp host
    fun changeUdpOutputHost(host: String) {
        _udpOutputHost.value = host
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.wakeWordOption.value = _wakeWordOption.value
        ConfigurationSetting.wakeWordPorcupineAccessToken.value = _wakeWordPorcupineAccessToken.value
        ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value = _wakeWordPorcupineKeywordDefaultOptions.value
        ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value = _wakeWordPorcupineKeywordCustomOptions.value
            .filter { !it.deleted }.map { it.keyword }.toSet()
        ConfigurationSetting.wakeWordPorcupineLanguage.value = _wakeWordPorcupineLanguage.value
        ConfigurationSetting.udpOutputHost.value = _udpOutputHost.value
        ConfigurationSetting.udpOutputPort.value = _udpOutputPort.value

        filesToDelete.forEach {
            FileUtils.removeFile(FileType.PORCUPINE, null, it)
        }
        filesToDelete.clear()
        newFiles.clear()
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _wakeWordOption.value = ConfigurationSetting.wakeWordOption.value
        _wakeWordPorcupineAccessToken.value = ConfigurationSetting.wakeWordPorcupineAccessToken.value
        _wakeWordPorcupineKeywordDefaultOptions.value = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value
        _wakeWordPorcupineKeywordCustomOptions.value = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value
            .map { PorcupineCustomKeywordUi(it) }
        _wakeWordPorcupineLanguage.value = ConfigurationSetting.wakeWordPorcupineLanguage.value
        _udpOutputHost.value = ConfigurationSetting.udpOutputHost.value
        _udpOutputPort.value = ConfigurationSetting.udpOutputPort.value

        newFiles.forEach {
            FileUtils.removeFile(FileType.PORCUPINE, null, it)
        }
        filesToDelete.clear()
    }


    override fun initializeTestParams() {
        get<WakeWordServiceParams> {
            parametersOf(
                WakeWordServiceParams(
                    wakeWordOption = _wakeWordOption.value,
                    wakeWordPorcupineAccessToken = _wakeWordPorcupineAccessToken.value,
                    wakeWordPorcupineKeywordDefaultOptions = _wakeWordPorcupineKeywordDefaultOptions.value,
                    wakeWordPorcupineKeywordCustomOptions = _wakeWordPorcupineKeywordCustomOptions.value
                        .filter { !it.deleted }.map { it.keyword }.toSet(),
                    wakeWordPorcupineLanguage = _wakeWordPorcupineLanguage.value
                )
            )
        }

        get<UdpServiceParams> {
            parametersOf(
                UdpServiceParams(
                    udpOutputHost = _udpOutputHost.value,
                    udpOutputPort = _udpOutputPort.value
                )
            )
        }
    }

    fun startWakeWordDetection() {
        testRunner.startWakeWordDetection()
    }

}