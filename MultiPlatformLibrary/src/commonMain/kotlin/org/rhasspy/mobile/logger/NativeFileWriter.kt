package org.rhasspy.mobile.logger

expect class NativeFileWriter(filename: String) {

    fun appendJsonElement(element: String)

    fun getFileContent(): String

}