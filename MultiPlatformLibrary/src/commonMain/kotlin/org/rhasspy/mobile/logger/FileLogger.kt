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
import org.rhasspy.mobile.nativeutils.FileWriter
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSetting

object FileLogger : LogWriter() {
    //create new file when logfile is 2 MB
    private val fileWriter = FileWriter("logfile.txt", 2000)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _flow = MutableSharedFlow<LogElement>()
    val flow = _flow.readOnly

    init {
        Logger.setMinSeverity(AppSetting.logLevel.value.severity)

        //create initial log file
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
            fileWriter.appendText(",${Json.encodeToString(element)}")
            _flow.emit(element)
        }
    }

    /**
     * read all lines from file
     */
    fun getLines(): List<LogElement> {
        return try {
            Json.decodeFromString("[${fileWriter.getFileContent()}]")
        } catch (exception: Exception) {
            fileWriter.clearFile()
            listOf()
        }
    }
//TODO delete content when corrupted (JsonDecodingException)
    /**
     * share the log file
     */
    fun shareLogFile() = fileWriter.shareFile()

    /**
     * save log to external file
     */
    fun saveLogFile() = fileWriter.copyFile(
        "rhasspy_logfile_${
            Clock.System.now().toLocalDateTime(TimeZone.UTC)
        }.txt", "text/txt"
    )

}