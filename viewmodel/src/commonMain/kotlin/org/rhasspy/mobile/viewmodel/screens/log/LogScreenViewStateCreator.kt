package org.rhasspy.mobile.viewmodel.screens.log

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import database.LogElements
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
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

    private fun getNews(): Flow<PagingData<LogElements>> {
        return Pager<Int, LogElements>(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 20,
                enablePlaceholders = true,
                initialLoadSize = 20 * 3,
                maxSize = Int.MAX_VALUE,
                jumpThreshold = Int.MIN_VALUE,
            ),
            pagingSourceFactory = { fileLogger.getPagingSource() }
        ).flow
    }

}
