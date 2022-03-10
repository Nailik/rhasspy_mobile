package org.rhasspy.mobile.logger

import org.rhasspy.mobile.Application
import java.io.File

actual class NativeFileWriter actual constructor(filename: String) {

    private val logfile = File(Application.Instance.filesDir, filename)

    init {
        if (!logfile.exists()) {
            logfile.createNewFile()
        }
    }

    actual fun appendLine(line: String) {
        logfile.appendText(line + "\n")
    }

}