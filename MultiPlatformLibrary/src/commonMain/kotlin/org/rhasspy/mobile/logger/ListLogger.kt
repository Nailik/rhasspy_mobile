package org.rhasspy.mobile.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

object ListLogger : LogWriter() {

    val logArr = mutableListOf<String>()

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {

        logArr.add("${severity.ordinal}:$tag:$message:${throwable?.message}")

        if (logArr.size > 1000) {
            logArr.removeAt(logArr.lastIndex)
        }

    }
}