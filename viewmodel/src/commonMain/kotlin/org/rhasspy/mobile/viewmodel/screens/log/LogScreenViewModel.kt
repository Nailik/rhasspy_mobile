package org.rhasspy.mobile.viewmodel.screens.log

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.*
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Change.ToggleListAutoScroll
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Navigate.SaveLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Navigate.ShareLogFile

@Stable
class LogScreenViewModel(
    viewStateCreator: LogScreenViewStateCreator
) : ViewModel() {

    private val _viewState: MutableStateFlow<LogScreenViewState> = viewStateCreator()
    val viewState = _viewState.readOnly

    fun onEvent(event: LogScreenUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Navigate -> onNavigate(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            ToggleListAutoScroll -> AppSetting.isLogAutoscroll.value = !AppSetting.isLogAutoscroll.value
        }
    }

    private fun onNavigate(navigate: Navigate) {
        when (navigate) {
            SaveLogFile -> {
                viewModelScope.launch(Dispatchers.Default) {
                    if (!FileLogger.saveLogFile()) {
                        _viewState.update {
                            it.copy(snackBarText = MR.strings.saveLogFileFailed.stable)
                        }
                    }
                }
            }

            ShareLogFile -> {
                if (!FileLogger.shareLogFile()) {
                    _viewState.update {
                        it.copy(snackBarText = MR.strings.shareLogFileFailed.stable)
                    }
                }
            }
        }
    }

    private fun onConsumed(consumed: Consumed) {
        _viewState.update {
            when (consumed) {
                ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

}