package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.services.state.ServiceState
import org.rhasspy.mobile.services.state.State
import org.rhasspy.mobile.services.state.StateType

abstract class IServiceTest(tag: String)  {

    internal val logger = Logger.withTag("$tag-Test")
    private val _currentState = MutableSharedFlow<ServiceState>()
    val currentState: SharedFlow<ServiceState> = _currentState

    private val scope = CoroutineScope(Dispatchers.Default)

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