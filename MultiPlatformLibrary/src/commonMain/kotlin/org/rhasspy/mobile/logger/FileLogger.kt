package org.rhasspy.mobile.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

object FileLogger : LogWriter() {

    private val nativeFileWriter = NativeFileWriter("logfile.txt")

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {

        //Severity Ordinal: tag: message: throwable?
        nativeFileWriter.appendLine("${severity.ordinal}:$tag:$message:${throwable?.message}")

    }
}