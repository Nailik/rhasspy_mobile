package org.rhasspy.mobile.logic.domains

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.pipeline.PipelineEvent

interface IDomainHistory {

    val historyState: StateFlow<List<PipelineEvent>>

    fun clearHistory()

}

class DomainHistory : IDomainHistory {


    override val historyState = MutableStateFlow<MutableList<PipelineEvent>>(mutableListOf())

    internal fun addToHistory(result: PipelineEvent) {
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