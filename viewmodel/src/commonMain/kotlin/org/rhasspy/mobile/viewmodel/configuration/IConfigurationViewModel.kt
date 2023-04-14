package org.rhasspy.mobile.viewmodel.configuration

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.logic.logger.LogElement
import org.rhasspy.mobile.logic.logger.LogLevel
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.ui.event.StateEvent
import org.rhasspy.mobile.ui.event.StateEvent.Triggered
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationEditUiAction
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationEditUiAction.BackPress
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationEditUiAction.Discard
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationEditUiAction.DismissDialog
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationEditUiAction.Save
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationEditUiAction.StartTest
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationEditUiAction.StopTest
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationTestUiAction
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationTestUiAction.ToggleListAutoscroll
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiAction.IConfigurationTestUiAction.ToggleListFiltered
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.PopBackStack
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

abstract class IConfigurationViewModel<T: IConfigurationTest, V: IConfigurationEditViewState>(
    private val service: IService,
    internal val testRunner: T,
    private val initialViewState: () -> V
) : ViewModel(), KoinComponent {
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
            logEvents = logEvents.mapReadonlyState { it.toImmutableList() }
        )
    )

    protected val contentViewState = MutableStateFlow(initialViewState())

    protected val data get() = contentViewState.value

    private val _viewState = MutableStateFlow(
        ConfigurationViewState(
            isBackPressDisabled = false,
            isLoading = false,
            serviceViewState = serviceViewState,
            editViewState = contentViewState,
            testViewState = configurationTestViewState
        )
    )
    val viewState = _viewState.readOnly

    fun onAction(action: IConfigurationUiAction) {
        when (action) {
            is IConfigurationEditUiAction -> onEditAction(action)
            is IConfigurationTestUiAction -> onTestAction(action)
        }
    }

    fun onConsumed(event: IConfigurationUiEvent) {
        when (event) {
            is PopBackStack -> _viewState.update { it.copy(popBackStack = PopBackStack(StateEvent.Consumed)) }
        }
    }

    private fun onEditAction(action: IConfigurationEditUiAction) {
        when (action) {
            Discard -> discard()
            Save -> save()
            StartTest -> startTest()
            StopTest -> stopTest()
            BackPress -> {
                if (contentViewState.value.hasUnsavedChanges) {
                    _viewState.update { it.copy(showUnsavedChangesDialog = true) }
                } else {
                    _viewState.update { it.copy(popBackStack = PopBackStack(Triggered))}
                }
            }

            DismissDialog -> _viewState.update { it.copy(showUnsavedChangesDialog = false)}
        }
    }

    private fun onTestAction(action: IConfigurationTestUiAction) {
        when (action) {
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
        }
    }

    private val isTestRunning = MutableStateFlow(false)
    private var testScope = CoroutineScope(Dispatchers.Default)

    fun save() {
        _viewState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.Default) {
            onSave()
            get<NativeApplication>().reloadServiceModules()

            if(_viewState.value.showUnsavedChangesDialog) {
                _viewState.update {
                    it.copy(
                        showUnsavedChangesDialog = false,
                        popBackStack = PopBackStack(Triggered)
                    )
                }
            } else {
                _viewState.update { it.copy(isLoading = false) }
            }

        }
    }

    private fun discard() {

        _viewState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.Default) {
            onDiscard()
            contentViewState.value = initialViewState()
            get<NativeApplication>().reloadServiceModules()

            if(_viewState.value.showUnsavedChangesDialog) {
                _viewState.update {
                    it.copy(
                        showUnsavedChangesDialog = false,
                        popBackStack = PopBackStack(Triggered)
                    )
                }
            } else {
                _viewState.update { it.copy(isLoading = false) }
            }
        }

    }

    open fun onDiscard() {}

    abstract fun onSave()

    abstract fun initializeTestParams()

    private fun startTest() {
        if (isTestRunning.value) {
            return
        }
        logger.d { "************* start Test ************" }

        isTestRunning.value = true
        _viewState.update { it.copy(isLoading = true) }

        testStartDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
        //set log type to debug minimum
        if (AppSetting.logLevel.value > LogLevel.Debug) {
            Logger.setMinSeverity(LogLevel.Debug.severity)
        }

        viewModelScope.launch(Dispatchers.Default) {
            testScope = CoroutineScope(Dispatchers.Default)

            //load file into list
            testScope.launch(Dispatchers.Default) {
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

            get<NativeApplication>().startTest()
            initializeTestParams()

            testRunner.initializeTest()

            _viewState.update { it.copy(isLoading = false) }
        }
    }

    private fun stopTest() {
        if (!isTestRunning.value) {
            return
        }
        logger.d { "************* stopTest ************" }

        //reset log level
        Logger.setMinSeverity(AppSetting.logLevel.value.severity)
        _viewState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.Default) {
            testScope.cancel()

            //reload koin modules when test is stopped
            get<NativeApplication>().stopTest()

            _viewState.update { it.copy(isLoading = false) }
            isTestRunning.value = false
        }
    }

}