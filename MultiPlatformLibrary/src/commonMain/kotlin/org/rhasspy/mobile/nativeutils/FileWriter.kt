package org.rhasspy.mobile.nativeutils

expect class FileWriter(filename: String, maxFileSize: Long) {

    val maxFileSize: Long

    fun createFile(): Boolean

    fun appendText(element: String)

    fun getFileContent(): String

    fun getFileData(): List<Byte>

    fun writeData(byteData: List<Byte>)

    fun shareFile()

    fun saveFile(fileName: String)

}