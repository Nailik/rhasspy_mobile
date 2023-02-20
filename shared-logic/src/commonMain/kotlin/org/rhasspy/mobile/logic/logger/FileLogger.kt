package org.rhasspy.mobile.logic.logger

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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.rhasspy.mobile.logic.nativeutils.FileWriterText
import org.rhasspy.mobile.logic.readOnly
import org.rhasspy.mobile.logic.settings.AppSetting

object FileLogger : LogWriter() {
    private val logger = Logger.withTag("FileLogger")
    //create new file when logfile is 2 MB
    private val fileWriterText = FileWriterText("logfile.txt", 2000)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _flow = MutableSharedFlow<LogElement>()
    val flow = _flow.readOnly

    init {
        Logger.setMinSeverity(AppSetting.logLevel.value.severity)

        //create initial log file
        if (fileWriterText.createFile()) {
            fileWriterText.appendText(
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
    }

    /**
     * override log function to append text to file
     */
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        coroutineScope.launch {
            val element = LogElement(
                Clock.System.now().toLocalDateTime(TimeZone.UTC).toString(),
                severity,
                tag,
                message,
                throwable?.message
            )
            fileWriterText.appendText(",${Json.encodeToString(element)}")
            _flow.emit(element)
        }
    }

    /**
     * read all lines from file
     */
    fun getLines(): List<LogElement> {
        return try {
            fileWriterText.decodeFromFile()
        } catch (exception: Exception) {
            logger.e(exception) { "could not read log file" }
            fileWriterText.clearFile()
            listOf()
        }
    }

    /**
     * share the log file
     */
    fun shareLogFile() = fileWriterText.shareFile()

    /**
     * save log to external file
     */
    fun saveLogFile() = fileWriterText.copyFile(
        "rhasspy_logfile_${
            Clock.System.now().toLocalDateTime(TimeZone.UTC)
        }.txt", "text/txt"
    )

}