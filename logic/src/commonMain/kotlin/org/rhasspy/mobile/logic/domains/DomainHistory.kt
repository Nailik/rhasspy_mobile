package org.rhasspy.mobile.logic.domains

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.pipeline.Result

interface IDomainHistory {

    val historyState: StateFlow<List<Result>>

    fun clearHistory()

}

class DomainHistory : IDomainHistory {


    override val historyState = MutableStateFlow<MutableList<Result>>(mutableListOf())

    internal fun addToHistory(result: Result) {
        historyState.update {
            it.apply {
                add(result)
            }
        }
    }

    override fun clearHistory() {
        historyState.value = mutableListOf()
    }


}