package org.rhasspy.mobile.viewModels.configuration

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.middleware.Event
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.serviceModule
import org.rhasspy.mobile.viewModels.configuration.test.IConfigurationTest

abstract class IConfigurationViewModel : ViewModel(), KoinComponent {

    private val logger = Logger.withTag("IConfigurationViewModel")

    protected abstract val testRunner: IConfigurationTest

    abstract val hasUnsavedChanges: StateFlow<Boolean>
    abstract val isTestingEnabled: StateFlow<Boolean>

    private val _events = MutableStateFlow<List<Event>>(listOf())
    val events = _events.readOnly

    private var testScope = CoroutineScope(Dispatchers.Default)

    fun save() {
        onSave()

        unloadKoinModules(serviceModule)
        loadKoinModules(serviceModule)
    }

    abstract fun discard()

    abstract fun onSave()

    abstract fun runTest()

    abstract fun initializeTestParams()

    //TODO carefully test this works correctly
    @Suppress("RedundantSuspendModifier")
    fun onOpenTestPage() {
        testScope = CoroutineScope(Dispatchers.Default)
        //needs to be suspend, else ui thread is blocked
        logger.e { "************* onOpenTestPage ************" }
        unloadKoinModules(serviceModule)
        loadKoinModules(serviceModule)
        initializeTestParams()
        testRunner.initializeTest()

        testScope.launch {
            testRunner.events.collect {
                _events.value = it
            }
        }
    }

    //TODO carefully test this works correctly
    @Suppress("RedundantSuspendModifier")
    fun stopTest() {
        testScope.cancel()
        //needs to be suspend, else ui thread is blocked
        logger.e { "************* stopTest ************" }
        //reload koin modules when test is stopped
        unloadKoinModules(serviceModule)
        loadKoinModules(serviceModule)
        Application.startServices()
    }

}