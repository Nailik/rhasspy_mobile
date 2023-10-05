package org.rhasspy.mobile.viewmodel.screens.dialog

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.pipeline.IPipelineManager
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenUiEvent.Change
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenUiEvent.Change.*

@Stable
class DialogScreenViewModel(
    private val pipelineManager: IPipelineManager
) : ScreenViewModel() {

    //  val viewState = viewStateCreator()

    fun onEvent(event: DialogScreenUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            ToggleListAutoScroll -> AppSetting.isDialogAutoscroll.value = !AppSetting.isDialogAutoscroll.value
            ManualListScroll     -> AppSetting.isDialogAutoscroll.value = false
            ClearHistory         -> Unit //TODO #466 dialogManagerService.clearHistory()
        }
    }

}
