package org.rhasspy.mobile.viewmodel.configuration

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
abstract class IConfigurationViewModel<V : IConfigurationEditViewState>(
    private val service: IService,
    private val initialViewState: () -> V
) : KViewModel() {
    private val logger = Logger.withTag("IConfigurationViewModel")
    private var testStartDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()

    private val serviceViewState = service.serviceState.mapReadonlyState {
        ServiceStateHeaderViewState(
            serviceState = ServiceViewState(service.serviceState),
            isOpenServiceDialogEnabled = (it is ServiceState.Exception || it is ServiceState.Error),
            serviceStateDialogText = when (it) {
                is ServiceState.Error -> it.information
                is ServiceState.Exception -> it.exception?.toString() ?: ""
                else -> ""
            }
        )
    }

    private val logEvents = MutableStateFlow<ImmutableList<LogElement>>(persistentListOf())
    private val configurationTestViewState = MutableStateFlow(
        ConfigurationTestViewState(
            isListFiltered = false,
            isListAutoscroll = true,
            logEvents = logEvents.mapReadonlyState { it.toImmutableList() },
            serviceViewState = serviceViewState
        )
    )

    protected val contentViewState = MutableStateFlow(initialViewState())

    protected val data get() = contentViewState.value

    private val _viewState = MutableStateFlow(
        ConfigurationViewState(
            isBackPressDisabled = false,
            serviceViewState = serviceViewState,
            editViewState = contentViewState,
            testViewState = configurationTestViewState,
            hasUnsavedChanges = false
        )
    )
    val viewState = _viewState.readOnly

    protected fun updateViewState(function: (V) -> V) {
        val newContentViewState = function(contentViewState.value)
        _viewState.update {
            it.copy(hasUnsavedChanges = newContentViewState != initialViewState())
        }
        contentViewState.value = newContentViewState
    }

    fun onAction(action: IConfigurationUiEvent) {
        when (action) {
            Discard -> discard(false)
            Save -> save(false)
            StartTest -> startTest()
            StopTest -> stopTest()
            BackPress -> {
                if (_viewState.value.hasUnsavedChanges) {
                    _viewState.update { it.copy(showUnsavedChangesDialog = true) }
                } else {
                    navigator.popBackStack()
                }
            }

            SaveDialog -> save(true)
            DiscardDialog -> discard(true)
            DismissDialog -> _viewState.update { it.copy(showUnsavedChangesDialog = false) }
            ToggleListAutoscroll -> configurationTestViewState.update { it.copy(isListAutoscroll = !it.isListAutoscroll) }
            ToggleListFiltered ->
                configurationTestViewState.update {
                    it.copy(isListFiltered = !it.isListFiltered)
                        .copy(
                            logEvents = logEvents.mapReadonlyState { events ->
                                if (it.isListFiltered) {
                                    events.filter { event -> event.tag == service.logType.name && event.time >= testStartDate }
                                        .toImmutableList()
                                } else {
                                    events.toImmutableList()
                                }
                            }
                        )
                }

            BackClick -> navigator.onBackPressed()
        }
    }

    private val isTestRunning = MutableStateFlow(false)
    protected var testScope = CoroutineScope(Dispatchers.IO)
        private set

    private fun save(popBackStack: Boolean) {
        updateData(popBackStack, ::onSave)
    }

    private fun discard(popBackStack: Boolean) {
        updateData(popBackStack, ::onDiscard)
    }

    private fun updateData(popBackStack: Boolean, function: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            function()
            contentViewState.value = initialViewState()
            noUnsavedChanges()
            if (popBackStack) {
                navigator.popBackStack()
            }
        }
    }

    private fun noUnsavedChanges() {
        _viewState.update {
            it.copy(
                showUnsavedChangesDialog = false,
                hasUnsavedChanges = false
            )
        }
    }

    protected abstract fun onDiscard()

    protected abstract fun onSave()

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

        viewModelScope.launch(Dispatchers.IO) {
            testScope = CoroutineScope(Dispatchers.IO)

            //load file into list
            testScope.launch(Dispatchers.IO) {
                val lines = FileLogger.getLines()
                viewModelScope.launch {
                    logEvents.value = lines
                }

                //collect new log
                FileLogger.flow.collectIndexed { _, value ->
                    viewModelScope.launch {
                        val list = mutableListOf<LogElement>()
                        list.addAll(logEvents.value)
                        list.add(value)
                        logEvents.value = list.toImmutableList()
                    }
                }
            }
        }
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

    override fun onBackPressed(): Boolean {
        onAction(BackPress)
        return true
    }

}