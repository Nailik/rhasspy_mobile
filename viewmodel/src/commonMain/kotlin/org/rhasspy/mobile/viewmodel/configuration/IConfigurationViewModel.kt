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
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.logic.update
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationUiAction
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationUiAction.IConfigurationEditUiAction
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationUiAction.IConfigurationEditUiAction.Discard
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationUiAction.IConfigurationEditUiAction.Save
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationUiAction.IConfigurationEditUiAction.StartTest
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationUiAction.IConfigurationEditUiAction.StopTest
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationUiAction.IConfigurationTestUiAction
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationUiAction.IConfigurationTestUiAction.ToggleListAutoscroll
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationUiAction.IConfigurationTestUiAction.ToggleListFiltered
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationViewState.IConfigurationTestViewState
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationViewState.ServiceStateHeaderViewState
import org.rhasspy.mobile.viewmodel.configuration.test.IConfigurationTest
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

abstract class IConfigurationViewModel<T: IConfigurationTest, V: IConfigurationContentViewState>(
    private val service: IService,
    internal val testRunner: T,
    private val logType: LogType,
    initialViewState: V
) : ViewModel(), KoinComponent {
    private val logger = Logger.withTag("IConfigurationViewModel")
    private var testStartDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()

    protected val contentViewState = MutableStateFlow(initialViewState)

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
        IConfigurationTestViewState(
            isListFiltered = false,
            isListAutoscroll = true,
            logEvents = logEvents.mapReadonlyState { it.toImmutableList() },
            serviceViewState = serviceViewState
        )
    )


    private val _viewState = MutableStateFlow(
        IConfigurationViewState(
            contentViewState = initialViewState,
            isBackPressDisabled = false,
            isLoading = false,
            editViewState = contentViewState.mapReadonlyState { it.getEditViewState(serviceViewState) },
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

    private fun onEditAction(action: IConfigurationEditUiAction) {
        when (action) {
            Discard -> discard()
            Save -> save()
            StartTest -> startTest()
            StopTest -> stopTest()
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
                                    events.filter { event -> event.tag == logType.name && event.time >= testStartDate }
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

            _viewState.update { it.copy(isLoading = false) }
        }
    }

    abstract fun discard()

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