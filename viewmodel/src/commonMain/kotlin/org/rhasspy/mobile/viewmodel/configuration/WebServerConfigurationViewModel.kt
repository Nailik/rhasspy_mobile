package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.openLink
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.logic.services.webserver.WebServerServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineAny
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.combineStateNotEquals
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.test.WebServerConfigurationTest

class WebServerConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<WebServerConfigurationTest>()
    override val logType = LogType.WebServerService
    override val serviceState get() = get<WebServerService>().serviceState

    //unsaved data
    private val _isHttpServerEnabled =
        MutableStateFlow(ConfigurationSetting.isHttpServerEnabled.value)
    private val _httpServerPort = MutableStateFlow(ConfigurationSetting.httpServerPort.value)
    private val _httpServerPortText =
        MutableStateFlow(ConfigurationSetting.httpServerPort.value.toString())
    private val _isHttpServerSSLEnabled =
        MutableStateFlow(ConfigurationSetting.isHttpServerSSLEnabledEnabled.value)
    private val _httpServerSSLKeyStoreFile =
        MutableStateFlow(ConfigurationSetting.httpServerSSLKeyStoreFile.value)
    private val _httpServerSSLKeyStorePassword =
        MutableStateFlow(ConfigurationSetting.httpServerSSLKeyStorePassword.value)
    private val _httpServerSSLKeyAlias =
        MutableStateFlow(ConfigurationSetting.httpServerSSLKeyAlias.value)
    private val _httpServerSSLKeyPassword =
        MutableStateFlow(ConfigurationSetting.httpServerSSLKeyPassword.value)

    //unsaved ui data
    val isHttpServerEnabled = _isHttpServerEnabled.readOnly
    val httpServerPortText = _httpServerPortText.readOnly
    val isHttpServerSSLEnabled = _isHttpServerSSLEnabled.readOnly
    val isHttpServerSettingsVisible = _isHttpServerEnabled.readOnly
    val isHttpServerSSLCertificateVisible = _isHttpServerSSLEnabled.readOnly

    val httpServerSSLKeyStoreFileText = _httpServerSSLKeyStoreFile.readOnly
    val isHttpServerSSLKeyStoreFileTextVisible =
        _httpServerSSLKeyStoreFile.mapReadonlyState { it != null }
    val httpServerSSLKeyStorePassword = _httpServerSSLKeyStorePassword.readOnly
    val httpServerSSLKeyAlias = _httpServerSSLKeyAlias.readOnly
    val httpServerSSLKeyPassword = _httpServerSSLKeyPassword.readOnly

    private val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_isHttpServerEnabled, ConfigurationSetting.isHttpServerEnabled.data),
        combineStateNotEquals(_httpServerPort, ConfigurationSetting.httpServerPort.data),
        combineStateNotEquals(_isHttpServerSSLEnabled, ConfigurationSetting.isHttpServerSSLEnabledEnabled.data),
        combineStateNotEquals(_httpServerSSLKeyStoreFile, ConfigurationSetting.httpServerSSLKeyStoreFile.data),
        combineStateNotEquals(_httpServerSSLKeyStorePassword, ConfigurationSetting.httpServerSSLKeyStorePassword.data),
        combineStateNotEquals(_httpServerSSLKeyAlias, ConfigurationSetting.httpServerSSLKeyAlias.data),
        combineStateNotEquals(_httpServerSSLKeyPassword, ConfigurationSetting.httpServerSSLKeyPassword.data)
    )

    override val configurationEditViewState = combineState(hasUnsavedChanges, _isHttpServerEnabled) { hasUnsavedChanges, isHttpServerEnabled ->
        IConfigurationViewState.IConfigurationEditViewState(
            hasUnsavedChanges = hasUnsavedChanges,
            isTestingEnabled = isHttpServerEnabled
        )
    }

    //toggle HTTP Server enabled
    fun toggleHttpServerEnabled(enabled: Boolean) {
        _isHttpServerEnabled.value = enabled
    }

    //edit port
    fun changeHttpServerPort(port: String) {
        val text = port.replace("""[-,. ]""".toRegex(), "")
        _httpServerPortText.value = text
        _httpServerPort.value = text.toIntOrNull() ?: 0
    }

    //Toggle http server ssl enabled
    fun toggleHttpServerSSLEnabled(enabled: Boolean) {
        _isHttpServerSSLEnabled.value = enabled
    }

    //open wiki page
    fun openWebServerSSLWiki() {
        openLink("https://github.com/Nailik/rhasspy_mobile/wiki/Webserver#enable-ssl")
    }

    //open file chooser to select certificate
    fun selectSSLCertificate() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.CertificateFolder.WebServer)?.also { fileName ->
                _httpServerSSLKeyStoreFile.value = fileName
            }
        }
    }

    //set keystore password
    fun changeHttpSSLKeyStorePassword(password: String) {
        _httpServerSSLKeyStorePassword.value = password
    }

    //set key alias
    fun changeHttpSSLKeyAlias(alias: String) {
        _httpServerSSLKeyAlias.value = alias
    }

    //set password for key alias
    fun changeHttpSSLKeyPassword(password: String) {
        _httpServerSSLKeyPassword.value = password
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        //delete old keystore file if changed
        if (_httpServerSSLKeyStoreFile.value != ConfigurationSetting.httpServerSSLKeyStoreFile.value) {
            ConfigurationSetting.httpServerSSLKeyStoreFile.value?.commonDelete()
        }

        ConfigurationSetting.isHttpServerEnabled.value = _isHttpServerEnabled.value
        ConfigurationSetting.httpServerPort.value = _httpServerPort.value
        ConfigurationSetting.isHttpServerSSLEnabledEnabled.value = _isHttpServerSSLEnabled.value
        ConfigurationSetting.httpServerSSLKeyStoreFile.value = _httpServerSSLKeyStoreFile.value
        ConfigurationSetting.httpServerSSLKeyStorePassword.value =
            _httpServerSSLKeyStorePassword.value
        ConfigurationSetting.httpServerSSLKeyAlias.value = _httpServerSSLKeyAlias.value
        ConfigurationSetting.httpServerSSLKeyPassword.value = _httpServerSSLKeyPassword.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        //delete new keystore file if changed
        if (_httpServerSSLKeyStoreFile.value != ConfigurationSetting.httpServerSSLKeyStoreFile.value) {
            _httpServerSSLKeyStoreFile.value?.commonDelete()
        }

        _isHttpServerEnabled.value = ConfigurationSetting.isHttpServerEnabled.value
        _httpServerPort.value = ConfigurationSetting.httpServerPort.value
        _httpServerPortText.value = ConfigurationSetting.httpServerPort.value.toString()
        _isHttpServerSSLEnabled.value = ConfigurationSetting.isHttpServerSSLEnabledEnabled.value
        _httpServerSSLKeyStoreFile.value = ConfigurationSetting.httpServerSSLKeyStoreFile.value
        _httpServerSSLKeyStorePassword.value =
            ConfigurationSetting.httpServerSSLKeyStorePassword.value
        _httpServerSSLKeyAlias.value = ConfigurationSetting.httpServerSSLKeyAlias.value
        _httpServerSSLKeyPassword.value = ConfigurationSetting.httpServerSSLKeyPassword.value
    }

    override fun initializeTestParams() {
        get<WebServerServiceParams> {
            parametersOf(
                WebServerServiceParams(
                    isHttpServerEnabled = _isHttpServerEnabled.value,
                    httpServerPort = _httpServerPort.value,
                    isHttpServerSSLEnabled = _isHttpServerSSLEnabled.value,
                    httpServerSSLKeyStoreFile = _httpServerSSLKeyStoreFile.value,
                    httpServerSSLKeyStorePassword = _httpServerSSLKeyStorePassword.value,
                    httpServerSSLKeyAlias = _httpServerSSLKeyAlias.value,
                    httpServerSSLKeyPassword = _httpServerSSLKeyPassword.value
                )
            )
        }
    }

}