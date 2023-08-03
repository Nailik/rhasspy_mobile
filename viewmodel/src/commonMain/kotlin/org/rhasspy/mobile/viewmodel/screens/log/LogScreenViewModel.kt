package org.rhasspy.mobile.viewmodel.screens.log

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.logger.IFileLogger
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.*
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.SaveLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.ShareLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Change.ManualListScroll
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Change.ToggleListAutoScroll
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Consumed.ShowSnackBar

@Stable
class LogScreenViewModel(
    private val fileLogger: IFileLogger,
    viewStateCreator: LogScreenViewStateCreator
) : ScreenViewModel() {

    private val dispatcher by inject<IDispatcherProvider>()

    private val _viewState: MutableStateFlow<LogScreenViewState> = viewStateCreator()
    val viewState = _viewState.readOnly

    fun onEvent(event: LogScreenUiEvent) {
        when (event) {
            is Change   -> onChange(event)
            is Action   -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            ToggleListAutoScroll -> AppSetting.isLogAutoscroll.value =
                !AppSetting.isLogAutoscroll.value

            ManualListScroll     -> AppSetting.isLogAutoscroll.value = false
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            SaveLogFile  -> {
                viewModelScope.launch(dispatcher.IO) {
                    if (!fileLogger.saveLogFile()) {
                        _viewState.update {
                            it.copy(snackBarText = MR.strings.saveLogFileFailed.stable)
                        }
                    }
                }
            }

            ShareLogFile -> {
                if (!fileLogger.shareLogFile()) {
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