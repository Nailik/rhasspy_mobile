package org.rhasspy.mobile.viewmodel.screens.log

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Change
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Change.ToggleListAutoScroll
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Navigate
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Navigate.SaveLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Navigate.ShareLogFile

@Stable
class LogScreenViewModel(
    viewStateCreator: LogScreenViewStateCreator
) : ViewModel() {

    val viewState: StateFlow<LogScreenViewState> = viewStateCreator()

    fun onEvent(event: LogScreenUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Navigate -> onNavigate(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            ToggleListAutoScroll -> AppSetting.isLogAutoscroll.value = !AppSetting.isLogAutoscroll.value
        }
    }

    private fun onNavigate(navigate: Navigate) {
        when (navigate) {
            SaveLogFile -> FileLogger.saveLogFile()
            ShareLogFile -> FileLogger.shareLogFile()
        }
    }

}