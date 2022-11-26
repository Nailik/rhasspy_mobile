package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.middleware.Event
import org.rhasspy.mobile.middleware.EventType
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.serviceModule
import kotlin.reflect.KClass

abstract class IConfigurationViewModel : ViewModel(), KoinComponent {

    abstract val hasUnsavedChanges: StateFlow<Boolean>
    abstract val isTestingEnabled: StateFlow<Boolean>

    private val _events = MutableStateFlow<List<Event>>(listOf())
    val events = _events.readOnly

    open val evenFilterType: KClass<*> = EventType.WebServerServiceEventType::class

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
        CoroutineScope(Dispatchers.Default).launch {
            testScope = CoroutineScope(Dispatchers.Default)
            unloadKoinModules(serviceModule)
            loadKoinModules(serviceModule)

            onTest()

            val middleware = get<IServiceMiddleware> { parametersOf(true) }

            testScope.launch {
                middleware.event.collect { event ->
                    _events.value = _events.value.toMutableList().also {
                        it.add(event)
                    }
                }
            }

            testScope.launch(Dispatchers.Default) {
                runTest()
            }
        }
    }

    fun stopTest() {
        CoroutineScope(Dispatchers.Default).launch {
            onStopTest()
            testScope.cancel()
            _events.value = listOf()
            unloadKoinModules(serviceModule)
            loadKoinModules(serviceModule)
        }
    }

}