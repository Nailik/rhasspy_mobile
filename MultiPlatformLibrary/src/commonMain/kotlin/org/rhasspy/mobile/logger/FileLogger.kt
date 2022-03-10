package org.rhasspy.mobile.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.rhasspy.mobile.settings.AppSettings

object FileLogger : LogWriter() {

    private val nativeFileWriter = NativeFileWriter("logfile.txt")

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {

        val element = LogElement(Clock.System.now().toLocalDateTime(TimeZone.UTC).toString(), severity, tag, message, throwable?.message)

        nativeFileWriter.appendJsonElement(Json.encodeToString(element))

        if (AppSettings.isShowLog.data) {
            ListLogger.addLog(element)
        }

    }

    fun getLines(): List<LogElement> = Json.decodeFromString("[${nativeFileWriter.getFileContent()}]")

}