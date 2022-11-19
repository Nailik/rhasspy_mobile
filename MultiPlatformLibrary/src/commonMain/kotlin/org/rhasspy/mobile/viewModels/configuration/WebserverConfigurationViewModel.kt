package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.state.ServiceState
import org.rhasspy.mobile.services.state.State
import org.rhasspy.mobile.services.webserver.WebServerLink
import org.rhasspy.mobile.services.webserver.data.WebServerServiceStateType.RECEIVING
import org.rhasspy.mobile.services.webserver.data.WebServerServiceStateType.STARTING
import org.rhasspy.mobile.settings.ConfigurationSettings

class WebserverConfigurationViewModel : ViewModel(), IConfigurationViewModel, KoinComponent {

    //unsaved data
    private val _isHttpServerEnabled =
        MutableStateFlow(ConfigurationSettings.isHttpServerEnabled.value)
    private val _httpServerPortText =
        MutableStateFlow(ConfigurationSettings.httpServerPort.value.toString())
    private val _httpServerPort = MutableStateFlow(ConfigurationSettings.httpServerPort.value)
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
        combineStateNotEquals(
            _isHttpServerSSLEnabled,
            ConfigurationSettings.isHttpServerSSLEnabled.data
        )
    )

    //toggle HTTP Server enabled
    fun toggleHttpServerEnabled(enabled: Boolean) {
        _isHttpServerEnabled.value = enabled
    }

    //edit port
    fun changeHttpServerPort(port: String) {
        val text = port.replace("""[-,. ]""".toRegex(), "")
        _httpServerPortText.value = text
        _httpServerPort.value = port.toIntOrNull() ?: 0
    }

    //Toggle http server ssl enabled
    fun toggleHttpServerSSLEnabled(enabled: Boolean) {
        _isHttpServerSSLEnabled.value = enabled
    }

    /**
     * save data configuration
     */
    override fun save() {
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


    //for testing
    private val webserver: WebServerLink by inject {
        parametersOf(
            _isHttpServerEnabled.value,
            _httpServerPort.value,
            _isHttpServerSSLEnabled.value
        )
    }
    private val _currentTestStartingState = MutableStateFlow<ServiceState?>(null)
    private val _currentTestReceivingStateList = MutableStateFlow(listOf<ServiceState>())

    override val testState = combineState(_currentTestStartingState, _currentTestReceivingStateList) { startingState, receivingStateList ->
        mutableListOf<ServiceState>().also { list ->
            startingState?.also {
                list.add(startingState)
            }
            list.addAll(receivingStateList)
        }
    }

    /**
     * test unsaved data configuration
     */
    override fun test() {
        CoroutineScope(Dispatchers.Default).launch {
            webserver.currentState.filterNotNull().collect { state ->
                when (state.stateType) {
                    STARTING -> {
                        _currentTestStartingState.value = state
                        _currentTestReceivingStateList.value = listOf(
                            ServiceState(State.Loading, RECEIVING)
                        )
                    }
                    RECEIVING -> {
                        //take last
                        val list = _currentTestReceivingStateList.value.toMutableList()
                        list.add(list.lastIndex, state)
                        _currentTestReceivingStateList.value = list
                    }
                }
            }
        }
        webserver.start()
    }

    override fun stopTest() {
        webserver.destroy()
    }

}