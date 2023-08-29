package org.rhasspy.mobile.viewmodel.screens.log

import androidx.compose.runtime.Stable
import app.cash.paging.PagingData
import database.LogElements
import kotlinx.coroutines.flow.Flow
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
data class LogScreenViewState internal constructor(
    val isLogAutoscroll: Boolean,
    val logList: Flow<PagingData<LogElements>>,
    val snackBarText: StableStringResource? = null
)