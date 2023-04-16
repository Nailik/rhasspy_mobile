package org.rhasspy.mobile.viewmodel.screens.log

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Change
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Change.ToggleListAutoScroll
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Navigate
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Navigate.SaveLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Navigate.ShareLogFile

class LogScreenViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(LogScreenViewState())
    val viewState = _viewState.readOnly

    init {
        //load file into list
        viewModelScope.launch(Dispatchers.Default) {
            val lines = FileLogger.getLines()
            _viewState.update {
                it.copy(logList = lines)
            }

            //collect new log
            FileLogger.flow.collectIndexed { _, value ->
                _viewState.update {
                    it.copy(
                        logList = it.logList.toMutableList().apply {
                            add(value)
                        }.toImmutableList()
                    )
                }
            }
        }
    }

    fun onEvent(event: LogScreenUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Navigate -> onNavigate(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                ToggleListAutoScroll -> {
                    val isLogAutoscroll = !it.isLogAutoscroll
                    AppSetting.isLogAutoscroll.value = isLogAutoscroll
                    it.copy(isLogAutoscroll = isLogAutoscroll)
                }
            }
        }
    }

    private fun onNavigate(navigate: Navigate) {
        when (navigate) {
            SaveLogFile -> FileLogger.saveLogFile()
            ShareLogFile -> FileLogger.shareLogFile()
        }
    }

}