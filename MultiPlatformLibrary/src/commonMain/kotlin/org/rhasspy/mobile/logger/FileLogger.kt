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
import org.rhasspy.mobile.services.native.FileWriter
import org.rhasspy.mobile.settings.AppSettings

object FileLogger : LogWriter() {
    private val logger = Logger.withTag(this::class.simpleName!!)

    val flow = MutableSharedFlow<LogElement>()

    //create new file when logfile is 2 MB
    private val fileWriter = FileWriter("logfile.txt", 2000)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        if (fileWriter.createFile()) {
            fileWriter.appendText(
                Json.encodeToString(
                    LogElement(
                        Clock.System.now().toLocalDateTime(TimeZone.UTC).toString(),
                        Severity.Verbose,
                        "NativeFileWriter",
                        "createdLogFile",
                        null
                    )
                )
            )
        }

        AppSettings.logLevel.value.addObserver {
            if (Logger.config.minSeverity != it.severity) {
                Logger.setMinSeverity(it.severity)
                logger.a { "changed log level to ${it.severity}" }
            }
        }
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {

        coroutineScope.launch {
            val element = LogElement(Clock.System.now().toLocalDateTime(TimeZone.UTC).toString(), severity, tag, message, throwable?.message)
            fileWriter.appendText(Json.encodeToString(element))
            flow.emit(element)
        }
    }

    fun getLines(): List<LogElement> = Json.decodeFromString("[${fileWriter.getFileContent()}]")

    fun shareLogFile() = fileWriter.shareFile()

}