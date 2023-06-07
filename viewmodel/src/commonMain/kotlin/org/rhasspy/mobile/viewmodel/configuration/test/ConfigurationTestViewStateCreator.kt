package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.logic.logger.FileLogger

//TODO updater scope needs to be cancelled somehow
class ConfigurationTestViewStateCreator(

) {
    private val updaterScope = CoroutineScope(Dispatchers.IO)

    private val viewState = MutableStateFlow()


    init {
        updaterScope.launch(Dispatchers.IO) {
            //load file into list
            val fileLogger = get<FileLogger>()
            val lines = fileLogger.getLines()
            logEvents.value = lines

            //collect new log
            fileLogger.flow.collectIndexed { _, value ->
                val list = mutableListOf<LogElement>()
                list.addAll(logEvents.value)
                list.add(value)
                logEvents.value = list.toImmutableList()
            }
        }
    }
}