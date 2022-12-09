package org.rhasspy.mobile.viewModels.configuration.test

import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.middleware.Event
import org.rhasspy.mobile.middleware.EventState
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.readOnly

abstract class IConfigurationTest : Closeable, KoinComponent {

    //test scope gets destroyed in stop test and recreated in start test
    protected var testScope = CoroutineScope(Dispatchers.Default)
        private set
    abstract val serviceState: StateFlow<EventState>

    //events of the test
    private val _events = MutableStateFlow<List<Event>>(listOf())
    val events = _events.readOnly

    fun initializeTest() {
        _events.value = emptyList()

        testScope = CoroutineScope(Dispatchers.Default)

        //load middleware
        val middleware = get<IServiceMiddleware> { parametersOf(true) }

        testScope.launch {
            middleware.event.collect { event ->
                _events.value = _events.value.toMutableList().also {
                    it.add(event)
                }
            }
        }
    }

    override fun close() {
        onClose()
        testScope.cancel()
        _events.value = listOf()
    }

    protected open fun onClose() {

    }

}