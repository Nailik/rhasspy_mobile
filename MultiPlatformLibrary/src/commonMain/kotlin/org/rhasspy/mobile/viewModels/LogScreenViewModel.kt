package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.postValue
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.logger.LogElement

class LogScreenViewModel : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    val logArr = MutableLiveData(listOf<LogElement>())

    init {
        //load file into list
        logArr.value = FileLogger.getLines().reversed()

        coroutineScope.launch {
            FileLogger.flow.collectIndexed { _, value ->
                val list = mutableListOf<LogElement>()
                list.addAll(logArr.value)
                list.add(0, value)
                logArr.postValue(list)
            }
        }
    }


}