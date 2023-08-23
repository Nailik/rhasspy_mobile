package org.rhasspy.mobile.viewmodel.screens.log

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.logger.IDatabaseLogger
import org.rhasspy.mobile.settings.AppSetting

class LogScreenViewStateCreator(
    private val fileLogger: IDatabaseLogger
) {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): MutableStateFlow<LogScreenViewState> {
        val viewState = MutableStateFlow(getViewState())
        //load file into list
        updaterScope.launch {
            AppSetting.isLogAutoscroll.data.collect { isLogAutoscroll ->
                viewState.update {
                    it.copy(
                        isLogAutoscroll = isLogAutoscroll,
                        logList = getNews()
                    )
                }
            }
        }
        return viewState
    }

    private fun getViewState(): LogScreenViewState {
        return LogScreenViewState(
            isLogAutoscroll = AppSetting.isLogAutoscroll.value,
            logList = emptyFlow()
        )
    }

    private fun getNews() = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { fileLogger.getPagingSource() }
    ).flow

}