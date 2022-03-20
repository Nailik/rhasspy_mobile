package org.rhasspy.mobile.services.native

expect class FileWriter(filename: String, maxFileSize: Long) {

    val maxFileSize: Long

    fun createFile(): Boolean

    fun appendText(element: String)

    fun getFileContent(): String

    fun getFileData(): List<Byte>

    fun writeData(byteData: List<Byte>)

    fun shareFile()

}