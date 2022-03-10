package org.rhasspy.mobile.logger

import dev.icerock.moko.mvvm.livedata.MutableLiveData

object ListLogger {

    val logArr = MutableLiveData(listOf<LogElement>())

    init {
        //load file into list
        logArr.value = FileLogger.getLines().reversed()
    }

    fun addLog(logElement: LogElement) {
        val list = mutableListOf<LogElement>()
        list.addAll(logArr.value)

        list.add(0, logElement)

        if (list.size > 1000) {
            list.removeAt(logArr.value.lastIndex)
        }

        logArr.value = list
    }
}