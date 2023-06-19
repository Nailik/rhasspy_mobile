package org.rhasspy.mobile.viewmodel.configuration.test

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
data class ConfigurationTestViewState internal constructor(
    val serviceViewState: ServiceViewState,
    val serviceTag: LogType,
    val isOpenServiceStateDialogEnabled: Boolean = false,
    val isListFiltered: Boolean = false,
    val isListAutoscroll: Boolean = true,
    val logList: ImmutableList<LogElement>,
    val dialogState: DialogState? = null
) {

    val visibleLogList
        get() = if (isListFiltered) {
            logList.filter { it.tag == serviceTag.name }
        } else logList

    sealed interface DialogState {

        data class ServiceStateDialog(val dialogText: Any) : DialogState

    }

}