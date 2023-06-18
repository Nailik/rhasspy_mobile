package org.rhasspy.mobile.viewmodel.configuration.test

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
data class ConfigurationTestViewState internal constructor(
    val serviceViewState: ServiceViewState,
    val isOpenServiceStateDialogEnabled: Boolean = false,
    val isListFiltered: Boolean = false,
    val isListAutoscroll: Boolean = true,
    val logEvents: StateFlow<ImmutableList<LogElement>>,
    val dialogState: DialogState? = null
) {

    sealed interface DialogState {

        data class ServiceStateDialog(val dialogText: Any): DialogState

    }

}