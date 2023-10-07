package org.rhasspy.mobile.viewmodel.screens.dialog

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenUiEvent.Change
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenUiEvent.Change.*

@Stable
class DialogScreenViewModel(
    viewStateCreator: DialogScreenViewStateCreator,
    private val userConnection: IUserConnection,
) : ScreenViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: DialogScreenUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            ToggleListAutoScroll -> AppSetting.isDialogAutoscroll.value = !AppSetting.isDialogAutoscroll.value
            ManualListScroll     -> AppSetting.isDialogAutoscroll.value = false
            ClearHistory         -> userConnection.clearPipelineHistory()
        }
    }

}
