package org.rhasspy.mobile.logic.domains

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.pipeline.DomainResult

interface IDomainHistory {

    val historyState: StateFlow<List<DomainResult>>

    fun clearHistory()

    fun addToHistory(result: DomainResult)

}

//TODO DomainState - Start - await - Result
class DomainHistory : IDomainHistory {

    private val logger = Logger.withTag("DomainHistory")

    override val historyState = MutableStateFlow<MutableList<DomainResult>>(mutableListOf())

    override fun addToHistory(result: DomainResult) {
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