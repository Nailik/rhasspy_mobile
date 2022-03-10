package org.rhasspy.mobile.logger

expect class NativeFileWriter(filename: String) {

    fun appendLine(line: String)

    fun getLines(): List<String>

}