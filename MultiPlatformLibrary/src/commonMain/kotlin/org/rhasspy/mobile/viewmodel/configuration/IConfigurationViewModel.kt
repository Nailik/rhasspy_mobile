package org.rhasspy.mobile.viewmodel.configuration

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.koin.serviceModule
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.logger.LogElement
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.viewmodel.configuration.test.IConfigurationTest

abstract class IConfigurationViewModel : ViewModel(), KoinComponent {
    private val logger = Logger.withTag("IConfigurationViewModel")

    protected abstract val testRunner: IConfigurationTest
    protected abstract val logType: LogType

    abstract val serviceState: StateFlow<ServiceState>
    abstract val hasUnsavedChanges: StateFlow<Boolean>
    abstract val isTestingEnabled: StateFlow<Boolean>

    private val _logEvents = MutableStateFlow(listOf<LogElement>())
    val logEvents: StateFlow<List<LogElement>>
        get() = combineState(_logEvents, _isListFiltered) { events, filtered ->
            if (filtered) {
                events.filter { it.tag == logType.name }
            } else {
                events
            }
        }

    private val _isListFiltered = MutableStateFlow(false)
    val isListFiltered = _isListFiltered.readOnly

    private val _isListAutoscroll = MutableStateFlow(false)
    val isListAutoscroll = _isListAutoscroll.readOnly

    private val isTestRunning = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.readOnly
    val isBackPressDisabled = isLoading

    private var testScope = CoroutineScope(Dispatchers.Default)

    fun toggleListFiltered() {
        _isListFiltered.value = !_isListFiltered.value
    }

    fun toggleListAutoscroll() {
        _isListAutoscroll.value = !_isListAutoscroll.value
    }


    fun save() {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.Default) {
            onSave()

            unloadKoinModules(serviceModule)
            loadKoinModules(serviceModule)

            _isLoading.value = false
        }
    }

    abstract fun discard()

    abstract fun onSave()

    abstract fun runTest()

    abstract fun initializeTestParams()

    //TODO carefully test this works correctly
    @Suppress("RedundantSuspendModifier")
    fun onOpenTestPage() {
        if (isTestRunning.value) {
            return
        }
        isTestRunning.value = true
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.Default) {
            testScope = CoroutineScope(Dispatchers.Default)
            //needs to be suspend, else ui thread is blocked
            logger.e { "************* onOpenTestPage ************" }


            //load file into list
            testScope.launch(Dispatchers.Default) {
                val lines = FileLogger.getLines().reversed()
                viewModelScope.launch {
                    _logEvents.value = lines
                }

                //TODO stop collection?
                //collect new log
                FileLogger.flow.collectIndexed { _, value ->
                    viewModelScope.launch {
                        val list = mutableListOf<LogElement>()
                        list.addAll(_logEvents.value)
                        list.add(value)
                        _logEvents.value = list
                    }
                }
            }


            Application.instance.startTest()
            initializeTestParams()

            testRunner.initializeTest()

            _isLoading.value = false
        }
    }

    //TODO carefully test this works correctly
    @Suppress("RedundantSuspendModifier")
    fun stopTest() {
        if (!isTestRunning.value) {
            return
        }
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.Default) {
            testScope.cancel()
            //needs to be suspend, else ui thread is blocked
            logger.e { "************* stopTest ************" }

            //reload koin modules when test is stopped
            Application.instance.stopTest()

            _isLoading.value = false
            isTestRunning.value = false
        }
    }
}
