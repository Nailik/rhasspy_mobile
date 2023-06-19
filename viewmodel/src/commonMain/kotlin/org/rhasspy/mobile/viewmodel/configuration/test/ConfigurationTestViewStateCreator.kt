package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.platformspecific.updateList

class ConfigurationTestViewStateCreator(
    private val fileLogger: FileLogger
) {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(
        configurationTestViewState: MutableStateFlow<ConfigurationTestViewState>
    ) {

        //load file into list
        updaterScope.launch {
            val lines = fileLogger.getLines()
            configurationTestViewState.update {
                it.copy(logList = lines)
            }

            //collect new log
            fileLogger.flow.collectIndexed { _, value ->
                configurationTestViewState.update {
                    it.copy(
                        logList = it.logList.updateList {
                            add(value)
                        }
                    )
                }
            }
        }

    }
}