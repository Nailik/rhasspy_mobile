package org.rhasspy.mobile.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import dev.icerock.moko.mvvm.livedata.MutableLiveData

object ListLogger : LogWriter() {

    val logArr = MutableLiveData(listOf<String>())

    init {
        //load file into list
        logArr.value = FileLogger.getLines().reversed()
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {

        val list = mutableListOf<String>()
        list.addAll(logArr.value)

        list.add(0, "${severity.ordinal}:$tag:$message:${throwable?.message}")

        if (list.size > 1000) {
            list.removeAt(logArr.value.lastIndex)
        }

        logArr.value = list
    }
}