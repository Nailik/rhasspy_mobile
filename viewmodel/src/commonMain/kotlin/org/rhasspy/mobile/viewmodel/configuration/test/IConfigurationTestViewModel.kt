package org.rhasspy.mobile.viewmodel.configuration.test

import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.get
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.configuration.test.IConfigurationTestUiEvent
import org.rhasspy.mobile.viewmodel.configuration.test.IConfigurationTestUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.test.IConfigurationTestUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.test.IConfigurationTestUiEvent.Action.*

class IConfigurationTestViewModel  : KViewModel() {

    private var testStartDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
    private val logEvents = MutableStateFlow<ImmutableList<LogElement>>(persistentListOf())

    private val _viewState = MutableStateFlow(
        IConfigurationTestViewState(
            isListFiltered = false,
            isListAutoscroll = true,
            logEvents = logEvents.mapReadonlyState { it.toImmutableList() }
        )
    )
    val viewState = _viewState.readOnly

    private val isTestRunning = MutableStateFlow(false)
    protected var testScope = CoroutineScope(Dispatchers.IO)
        private set

    fun onAction(action: IConfigurationTestUiEvent) {
        when (action) {
            StartTest -> startTest()
            StopTest -> stopTest()
            BackClick -> navigator.onBackPressed()
            CloseServiceStateDialog -> _viewState.update { it.copy(isShowServiceStateDialog = false) }
            OpenServiceStateDialog -> _viewState.update { it.copy(isShowServiceStateDialog = true) }
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