package org.rhasspy.mobile.viewmodel.configuration.test

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.get
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.test.ConfigurationTestViewState.DialogState.ServiceStateDialog
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
class IConfigurationTestViewModel(
    service: IService
) : KViewModel() {

    private val logger = Logger.withTag("IConfigurationTestViewModel")

    private var testStartDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
    private val logEvents = MutableStateFlow<ImmutableList<LogElement>>(persistentListOf())

    private val _configurationTestViewState = MutableStateFlow(
        ConfigurationTestViewState(
            isListFiltered = false,
            isListAutoscroll = true,
            serviceViewState = ServiceViewState(service.serviceState),
            logEvents = logEvents
        )
    )
    val configurationTestViewState get() = _configurationTestViewState.readOnly

    private val isTestRunning = MutableStateFlow(false)
    private var testScope = CoroutineScope(Dispatchers.IO)
        private set

    fun onEvent(event: ConfigurationTestUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is DialogAction -> onDialog(event)
            is Change -> onChange(event)
        }
    }

    private fun onAction(action: Action) {
        when(action) {
            BackClick -> navigator.onBackPressed()
        }
    }

    private fun onDialog(dialogAction: DialogAction) {

        _configurationTestViewState.update { it.copy(dialogState = null) }

        when(dialogAction.dialogState) {
            is ServiceStateDialog -> Unit
        }

    }

    private fun onChange(change: Change) {
        _configurationTestViewState.update {
            when (change) {
                OpenServiceStateDialog -> it.copy(dialogState = ServiceStateDialog(it.serviceViewState.serviceState.value))
                ToggleListAutoscroll -> it.copy(isListAutoscroll = !it.isListAutoscroll)
                ToggleListFiltered -> it.copy(isListFiltered = !it.isListFiltered)
            }
        }
    }

    private fun startTest() {
        if (isTestRunning.value) {
            return
        }
        logger.d { "************* start Test ************" }

        isTestRunning.value = true

        testStartDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
        //set log type to debug minimum
        if (AppSetting.logLevel.value > LogLevel.Debug) {
            Logger.setMinSeverity(LogLevel.Debug.severity)
        }

        testScope = CoroutineScope(Dispatchers.IO)
    }

    private fun stopTest() {
        if (!isTestRunning.value) {
            return
        }
        logger.d { "************* stopTest ************" }

        //reset log level
        Logger.setMinSeverity(AppSetting.logLevel.value.severity)

        viewModelScope.launch(Dispatchers.IO) {
            testScope.cancel()
            isTestRunning.value = false
        }
    }

    protected suspend fun awaitMqttServiceStarted() {
        get<MqttService>()
            .isHasStarted
            .map { it }
            .distinctUntilChanged()
            .first { it }
    }


}