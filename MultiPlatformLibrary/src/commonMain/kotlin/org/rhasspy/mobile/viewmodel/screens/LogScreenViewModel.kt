package org.rhasspy.mobile.viewmodel.screens

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.logger.LogElement
import org.rhasspy.mobile.readOnly

class LogScreenViewModel : ViewModel() {
    val logArr = MutableStateFlow(listOf<LogElement>())

    private val _isListAutoscroll = MutableStateFlow(false)
    val isListAutoscroll = _isListAutoscroll.readOnly

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
        //TODO save to settings
        _isListAutoscroll.value = !_isListAutoscroll.value
    }

    fun shareLogFile() = FileLogger.shareLogFile()

    fun saveLogFile() = FileLogger.saveLogFile()

}