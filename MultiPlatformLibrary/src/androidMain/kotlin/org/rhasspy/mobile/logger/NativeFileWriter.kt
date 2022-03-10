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
        logfile.appendText(System.currentTimeMillis().toString() + " " + line + "\n")
    }

    actual fun getLines(): List<String> {
        val list = mutableListOf<String>()
        logfile.forEachLine {
            list.add(0, it)
        }
        return list
    }

}