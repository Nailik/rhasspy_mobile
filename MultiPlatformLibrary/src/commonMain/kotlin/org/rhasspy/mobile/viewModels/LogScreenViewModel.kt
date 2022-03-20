package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.logger.LogElement

class LogScreenViewModel : ViewModel() {
    val logArr = MutableLiveData(listOf<LogElement>())

    init {
        //load file into list
        CoroutineScope(Dispatchers.Default).launch {
            val lines = FileLogger.getLines().reversed()
            viewModelScope.launch {
                logArr.value = lines
            }

            //collect new log
            FileLogger.flow.collectIndexed { _, value ->
                viewModelScope.launch {
                    val list = mutableListOf<LogElement>()
                    list.addAll(logArr.value)
                    list.add(0, value)
                    logArr.value = list
                }
            }
        }
    }


}