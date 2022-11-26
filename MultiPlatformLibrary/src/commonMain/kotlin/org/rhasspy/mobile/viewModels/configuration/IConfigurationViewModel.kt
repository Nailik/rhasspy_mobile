package org.rhasspy.mobile.viewModels.configuration

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
import org.rhasspy.mobile.middleware.Event
import org.rhasspy.mobile.serviceModule

abstract class IConfigurationViewModel : ViewModel(), KoinComponent {

    abstract val hasUnsavedChanges: StateFlow<Boolean>
    abstract val isTestingEnabled: StateFlow<Boolean>

    val events = MutableStateFlow<List<Event>>(listOf())

    private var testScope = CoroutineScope(Dispatchers.Default)

    fun save() {
        onSave()

        unloadKoinModules(serviceModule)
        loadKoinModules(serviceModule)
    }

    abstract fun discard()

    abstract fun onSave()

    abstract fun onTest()

    open suspend fun runTest() {

    }

    open fun onStopTest() {

    }

    fun test() {
        testScope = CoroutineScope(Dispatchers.Default)
        unloadKoinModules(serviceModule)
        loadKoinModules(serviceModule)

        val eventFlow = onTest()
        testScope.launch {

        }
        testScope.launch {
            runTest()
        }
    }

    fun stopTest() {
        onStopTest()
        testScope.cancel()
        unloadKoinModules(serviceModule)
        loadKoinModules(serviceModule)
    }

}