package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.logger.Event
import org.rhasspy.mobile.logger.EventLogger
import org.rhasspy.mobile.logger.EventTag
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.webserver.WebServerService
import org.rhasspy.mobile.services.webserver.WebServerServiceParams
import org.rhasspy.mobile.settings.ConfigurationSettings

class WebServerConfigurationViewModel : IConfigurationViewModel() {

    //unsaved data
    private val _isHttpServerEnabled =
        MutableStateFlow(ConfigurationSettings.isHttpServerEnabled.value)
    private val _httpServerPort = MutableStateFlow(ConfigurationSettings.httpServerPort.value)
    private val _httpServerPortText =
        MutableStateFlow(ConfigurationSettings.httpServerPort.value.toString())
    private val _isHttpServerSSLEnabled =
        MutableStateFlow(ConfigurationSettings.isHttpServerSSLEnabled.value)

    //unsaved ui data
    val isHttpServerEnabled = _isHttpServerEnabled.readOnly
    val httpServerPortText = _httpServerPortText.readOnly
    val isHttpServerSSLEnabled = _isHttpServerSSLEnabled.readOnly
    val isHttpServerSettingsVisible = _isHttpServerEnabled.readOnly
    val isHttpServerSSLCertificateVisible = _isHttpServerSSLEnabled.readOnly
    override val isTestingEnabled = _isHttpServerEnabled.readOnly

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_isHttpServerEnabled, ConfigurationSettings.isHttpServerEnabled.data),
        combineStateNotEquals(_httpServerPort, ConfigurationSettings.httpServerPort.data),
        combineStateNotEquals(_isHttpServerSSLEnabled, ConfigurationSettings.isHttpServerSSLEnabled.data)
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

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSettings.isHttpServerEnabled.value = _isHttpServerEnabled.value
        ConfigurationSettings.httpServerPort.value = _httpServerPort.value
        ConfigurationSettings.isHttpServerSSLEnabled.value = _isHttpServerSSLEnabled.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _isHttpServerEnabled.value = ConfigurationSettings.isHttpServerEnabled.value
        _httpServerPort.value = ConfigurationSettings.httpServerPort.value
        _isHttpServerSSLEnabled.value = ConfigurationSettings.isHttpServerSSLEnabled.value
    }

    /**
     * test unsaved data configuration
     */
    override fun onTest(): StateFlow<List<Event>> {
        //initialize test params
        get<WebServerServiceParams> {
            parametersOf(
                WebServerServiceParams(
                    isHttpServerEnabled = _isHttpServerEnabled.value,
                    httpServerPort = _httpServerPort.value,
                    isHttpServerSSLEnabled = _isHttpServerSSLEnabled.value
                )
            )
        }
        //start web server
        get<WebServerService>()
        //get logger
        val eventLogger by inject<EventLogger>(named(EventTag.WebServerService.name))
        return eventLogger.events
    }

}