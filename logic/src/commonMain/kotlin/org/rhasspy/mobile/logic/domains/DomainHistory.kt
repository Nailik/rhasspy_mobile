package org.rhasspy.mobile.logic.domains

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.pipeline.DomainResult

interface IDomainHistory {

    val historyState: StateFlow<List<DomainResult>>

    fun clearHistory()

    fun addToHistory(start: DomainResult?, result: DomainResult)

}

//TODO DomainState - Start - await - Result
internal class DomainHistory(
    private val mqttConnection: IMqttConnection
) : IDomainHistory {

    private val logger = Logger.withTag("DomainHistory")

    override val historyState = MutableStateFlow<List<DomainResult>>(mutableListOf())

    override fun addToHistory(start: DomainResult?, result: DomainResult) {
        mqttConnection.notify(start, result)
        logger.d { "$result" }
        historyState.update {
            it.toMutableList().apply {
                add(result)
            }
        }
    }

    override fun clearHistory() {
        historyState.value = mutableListOf()
    }


}