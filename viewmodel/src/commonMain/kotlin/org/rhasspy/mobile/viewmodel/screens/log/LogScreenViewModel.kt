package org.rhasspy.mobile.viewmodel.screens.log

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.logic.logger.LogElement
import org.rhasspy.mobile.logic.settings.AppSetting

class LogScreenViewModel : ViewModel() {
    val logArr = MutableStateFlow(listOf<LogElement>())

    val isListAutoscroll = AppSetting.isLogAutoscroll.data

    init {
        //load file into list
        CoroutineScope(Dispatchers.Default).launch {
            val lines = FileLogger.getLines()
            viewModelScope.launch {
                logArr.value = lines
            }

            //collect new log
            FileLogger.flow.collectIndexed { _, value ->
                viewModelScope.launch {
                    val list = mutableListOf<LogElement>()
                    list.addAll(logArr.value)
                    list.add(value)
                    logArr.value = list
                }
            }
        }
    }

    fun toggleListAutoscroll() {
        AppSetting.isLogAutoscroll.value = !AppSetting.isLogAutoscroll.value
    }

    fun shareLogFile() = FileLogger.shareLogFile()

    fun saveLogFile() = FileLogger.saveLogFile()

}