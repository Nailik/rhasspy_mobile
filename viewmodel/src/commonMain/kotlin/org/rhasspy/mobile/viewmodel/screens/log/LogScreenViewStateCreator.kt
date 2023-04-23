package org.rhasspy.mobile.viewmodel.screens.log

import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.updateList

class LogScreenViewStateCreator {

    private val updaterScope = CoroutineScope(Dispatchers.Default)

    operator fun invoke(): StateFlow<LogScreenViewState> {
        val viewState = MutableStateFlow(getViewState())
        //load file into list
        updaterScope.launch {
            val lines = FileLogger.getLines()
            viewState.update {
                it.copy(logList = lines)
            }

            //collect new log
            FileLogger.flow.collectIndexed { _, value ->
                viewState.update {
                    it.copy(
                        logList = it.logList.updateList {
                            add(value)
                        }
                    )
                }
            }
        }

        updaterScope.launch {
            AppSetting.isLogAutoscroll.data.collect { isLogAutoscroll ->
                viewState.update {
                    it.copy(isLogAutoscroll = isLogAutoscroll)
                }
            }
        }
        return viewState
    }

    private fun getViewState(): LogScreenViewState {
        return LogScreenViewState(
            isLogAutoscroll = AppSetting.isLogAutoscroll.value,
            logList = persistentListOf()
        )
    }

}