package org.rhasspy.mobile.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.rhasspy.mobile.settings.AppSettings

object FileLogger : LogWriter() {
    private val logger = Logger.withTag(this::class.simpleName!!)

    val flow = MutableSharedFlow<LogElement>()

    private val nativeFileWriter = NativeFileWriter("logfile.txt")
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        AppSettings.logLevel.value.addObserver {
            if (Logger.config.minSeverity != it.severity) {
                Logger.setMinSeverity(it.severity)
                logger.a { "changed log level to ${it.severity}" }
            }
        }
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {

        val element = LogElement(Clock.System.now().toLocalDateTime(TimeZone.UTC).toString(), severity, tag, message, throwable?.message)

        nativeFileWriter.appendJsonElement(Json.encodeToString(element))

        coroutineScope.launch {
            flow.emit(element)
        }
    }

    fun getLines(): List<LogElement> = Json.decodeFromString("[${nativeFileWriter.getFileContent()}]")

}