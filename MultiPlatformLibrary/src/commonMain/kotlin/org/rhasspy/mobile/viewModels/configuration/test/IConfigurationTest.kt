package org.rhasspy.mobile.viewModels.configuration.test

import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.middleware.Event
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.serviceModule

abstract class IConfigurationTest : Closeable, KoinComponent {

    //test scope gets destroyed in stop test and recreated in start test
    private var testScope = CoroutineScope(Dispatchers.Default)

    //events of the test
    private val _events = MutableStateFlow<List<Event>>(listOf())
    val events = _events.readOnly

    //TODO test data in start test
    protected open fun startTest() {
        testScope = CoroutineScope(Dispatchers.Default)

        testScope.launch {
            //reload koin modules
            unloadKoinModules(serviceModule)
            loadKoinModules(serviceModule)

            //initialize koin params
            initializeTest()
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

        //run the actual test
        onTest(testScope)
    }

    override fun close() {
        onClose()
        testScope.cancel()
        _events.value = listOf()
    }

    /**
     * initialize koin params
     */
    protected abstract fun initializeTest()

    protected abstract fun onTest(scope: CoroutineScope)

    protected abstract fun runTest(scope: CoroutineScope)

    protected abstract fun onClose()

}