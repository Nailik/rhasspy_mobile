package org.rhasspy.mobile.logic.domains

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.pipeline.PipelineEvent

interface IDomainHistory {

    val historyState: StateFlow<List<PipelineEvent>>

    fun clearHistory()

    fun addToHistory(result: PipelineEvent)

}

class DomainHistory : IDomainHistory {

    private val logger = Logger.withTag("DomainHistory")

    override val historyState = MutableStateFlow<MutableList<PipelineEvent>>(mutableListOf())

    override fun addToHistory(result: PipelineEvent) {
        logger.d { "$result" }
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