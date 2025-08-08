package org.rhasspy.mobile.viewmodel.screens.dialog

import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenUiEvent.Change
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenUiEvent.Change.ManualListScroll
import org.rhasspy.mobile.viewmodel.screens.dialog.DialogScreenUiEvent.Change.ToggleListAutoScroll

class DialogScreenViewModel(
    viewStateCreator: DialogScreenViewStateCreator,
    private val dialogManagerService: IDialogManagerService
) : ScreenViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: DialogScreenUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            ToggleListAutoScroll -> AppSetting.isDialogAutoscroll.value =
                !AppSetting.isDialogAutoscroll.value

            ManualListScroll -> AppSetting.isDialogAutoscroll.value = false
            else -> dialogManagerService.clearHistory()
        }
    }

}
