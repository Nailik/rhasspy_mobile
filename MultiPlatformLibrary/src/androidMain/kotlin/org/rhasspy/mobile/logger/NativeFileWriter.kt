package org.rhasspy.mobile.logger

import co.touchlab.kermit.Severity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.rhasspy.mobile.Application
import java.io.File

actual class NativeFileWriter actual constructor(filename: String) {

    private val logfile = File(Application.Instance.filesDir, filename)

    init {
        if (!logfile.exists()) {
            logfile.createNewFile()
            logfile.appendText(
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

    actual fun appendJsonElement(element: String) {
        logfile.appendText(",\n$element")

        if (logfile.length() / 1024 >= 2000) {
            //create new file when logfile is 2 MB
            val oldFile = File("${logfile.parent}/${logfile.nameWithoutExtension}_old.${logfile.extension}")
            if (oldFile.exists()) {
                oldFile.delete()
            }
            logfile.copyTo(oldFile)
        }
    }

    actual fun getFileContent() = logfile.readText()

}