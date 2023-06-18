package org.rhasspy.mobile.viewmodel.configuration.test

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
abstract class ConfigurationTestViewState internal constructor(
    val serviceViewState: ServiceViewState,
    val isOpenServiceStateDialogEnabled: Boolean = false,
    val isListFiltered: Boolean = false,
    val isListAutoscroll: Boolean = true,
    val logEvents: StateFlow<ImmutableList<LogElement>>,
    val dialog: Dialogs? = null
) {

    sealed interface Dialogs {

        data class ServiceStateDialog(val dialogText: Any): Dialogs

    }

}