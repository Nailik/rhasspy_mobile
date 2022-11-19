package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.services.state.ServiceState
import org.rhasspy.mobile.services.state.State
import org.rhasspy.mobile.services.state.StateType

/**
 * Defines a Service Test
 *
 * handles start and stop of scope and start service link
 * also handles stop of scope and destroy of service link
 * handles stop and start of main service, that is retrieved via getService
 *
 * service link is given as parameter
 * in on start test necessary tasks should be created
 *
 * contains the current service state in a shared flow
 * currentError of service is always null
 */
abstract class IServiceTest(
    tag: String,
    private val serviceLink: IServiceLink
) : IService(), KoinComponent {

    internal val logger = Logger.withTag("$tag-Test")
    private val _currentState = MutableSharedFlow<ServiceState>()
    val currentState: SharedFlow<ServiceState> = _currentState

    private val scope = CoroutineScope(Dispatchers.Default)

    abstract fun getService(): IService

    abstract fun onStartTest(scope: CoroutineScope)

    override fun onStart(scope: CoroutineScope): IServiceLink {
        getService().stop()
        onStartTest(scope)
        return serviceLink
    }

    override fun onStopped() {
        getService().start()
    }

    //tests have no error
    override val currentError: SharedFlow<ServiceError?>
        get() = MutableSharedFlow()

    internal fun pending(stateType: StateType, description: Any? = null) {
        emitState(State.Pending, stateType, description)
    }

    internal fun loading(stateType: StateType, description: Any? = null) {
        emitState(State.Loading, stateType, description)
    }

    internal fun error(stateType: StateType, description: Any? = null) {
        emitState(State.Error, stateType, description)
    }

    internal fun success(stateType: StateType, description: Any? = null) {
        emitState(State.Success, stateType, description)
    }

    private fun emitState(
        state: State,
        stateType: StateType,
        description: Any? = null
    ) {
        scope.launch {
            _currentState.emit(ServiceState(state, stateType, description))
            logger.d { "new state $state at $stateType with $description" }
        }
    }

}