package org.rhasspy.mobile.viewmodel.screens.log

import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.logger.IFileLogger
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.settings.AppSetting

class LogScreenViewStateCreator(
    private val fileLogger: IFileLogger
) {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): MutableStateFlow<LogScreenViewState> {
        val viewState = MutableStateFlow(getViewState())
        //load file into list
        updaterScope.launch {
            val lines = fileLogger.getLines()
            viewState.update {
                it.copy(logList = lines)
            }

            //collect new log
            fileLogger.flow.collectIndexed { _, value ->
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