package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.FileUtils
import org.rhasspy.mobile.nativeutils.openLink
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.webserver.WebServerServiceParams
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.settings.FileType
import org.rhasspy.mobile.viewModels.configuration.test.WebServerConfigurationTest

class WebServerConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<WebServerConfigurationTest>()

    //unsaved data
    private val _isHttpServerEnabled =
        MutableStateFlow(ConfigurationSettings.isHttpServerEnabled.value)
    private val _httpServerPort = MutableStateFlow(ConfigurationSettings.httpServerPort.value)
    private val _httpServerPortText =
        MutableStateFlow(ConfigurationSettings.httpServerPort.value.toString())
    private val _isHttpServerSSLEnabled =
        MutableStateFlow(ConfigurationSettings.isHttpServerSSLEnabled.value)
    private val _httpServerSSLKeyStoreFile =
        MutableStateFlow(ConfigurationSettings.httpServerSSLKeyStoreFile.value)
    private val _httpServerSSLKeyStorePassword =
        MutableStateFlow(ConfigurationSettings.httpServerSSLKeyStorePassword.value)
    private val _httpServerSSLKeyAlias =
        MutableStateFlow(ConfigurationSettings.httpServerSSLKeyAlias.value)
    private val _httpServerSSLKeyPassword =
        MutableStateFlow(ConfigurationSettings.httpServerSSLKeyPassword.value)

    //unsaved ui data
    val isHttpServerEnabled = _isHttpServerEnabled.readOnly
    val httpServerPortText = _httpServerPortText.readOnly
    val isHttpServerSSLEnabled = _isHttpServerSSLEnabled.readOnly
    val isHttpServerSettingsVisible = _isHttpServerEnabled.readOnly
    val isHttpServerSSLCertificateVisible = _isHttpServerSSLEnabled.readOnly

    val httpServerSSLKeyStoreFileText = _httpServerSSLKeyStoreFile.readOnly
    val isHttpServerSSLKeyStoreFileTextVisible = _httpServerSSLKeyStoreFile.mapReadonlyState { it.isNotEmpty() }
    val httpServerSSLKeyStorePassword = _httpServerSSLKeyStorePassword.readOnly
    val httpServerSSLKeyAlias = _httpServerSSLKeyAlias.readOnly
    val httpServerSSLKeyPassword = _httpServerSSLKeyPassword.readOnly

    override val isTestingEnabled = _isHttpServerEnabled.readOnly

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_isHttpServerEnabled, ConfigurationSettings.isHttpServerEnabled.data),
        combineStateNotEquals(_httpServerPort, ConfigurationSettings.httpServerPort.data),
        combineStateNotEquals(_isHttpServerSSLEnabled, ConfigurationSettings.isHttpServerSSLEnabled.data),
        combineStateNotEquals(_httpServerSSLKeyStoreFile, ConfigurationSettings.httpServerSSLKeyStoreFile.data),
        combineStateNotEquals(_httpServerSSLKeyStorePassword, ConfigurationSettings.httpServerSSLKeyStorePassword.data),
        combineStateNotEquals(_httpServerSSLKeyAlias, ConfigurationSettings.httpServerSSLKeyAlias.data),
        combineStateNotEquals(_httpServerSSLKeyPassword, ConfigurationSettings.httpServerSSLKeyPassword.data)
    )

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
            FileUtils.selectFile(FileType.CERTIFICATE)?.also { fileName ->
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
        if (_httpServerSSLKeyStoreFile.value != ConfigurationSettings.httpServerSSLKeyStoreFile.value) {
            ConfigurationSettings.httpServerSSLKeyStoreFile.value.also {
                FileUtils.removeFile(
                    fileType = FileType.CERTIFICATE,
                    fileName = it
                )
            }
        }

        ConfigurationSettings.isHttpServerEnabled.value = _isHttpServerEnabled.value
        ConfigurationSettings.httpServerPort.value = _httpServerPort.value
        ConfigurationSettings.isHttpServerSSLEnabled.value = _isHttpServerSSLEnabled.value
        ConfigurationSettings.httpServerSSLKeyStoreFile.value = _httpServerSSLKeyStoreFile.value
        ConfigurationSettings.httpServerSSLKeyStorePassword.value = _httpServerSSLKeyStorePassword.value
        ConfigurationSettings.httpServerSSLKeyAlias.value = _httpServerSSLKeyAlias.value
        ConfigurationSettings.httpServerSSLKeyPassword.value = _httpServerSSLKeyPassword.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        //delete new keystore file if changed
        if (_httpServerSSLKeyStoreFile.value != ConfigurationSettings.httpServerSSLKeyStoreFile.value) {
            _httpServerSSLKeyStoreFile.value.also {
                FileUtils.removeFile(
                    fileType = FileType.CERTIFICATE,
                    fileName = it
                )
            }
        }

        _isHttpServerEnabled.value = ConfigurationSettings.isHttpServerEnabled.value
        _httpServerPort.value = ConfigurationSettings.httpServerPort.value
        _isHttpServerSSLEnabled.value = ConfigurationSettings.isHttpServerSSLEnabled.value
        _httpServerSSLKeyStoreFile.value = ConfigurationSettings.httpServerSSLKeyStoreFile.value
        _httpServerSSLKeyStorePassword.value = ConfigurationSettings.httpServerSSLKeyStorePassword.value
        _httpServerSSLKeyAlias.value = ConfigurationSettings.httpServerSSLKeyAlias.value
        _httpServerSSLKeyPassword.value = ConfigurationSettings.httpServerSSLKeyPassword.value
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

    override fun runTest() = testRunner.runTest()

}