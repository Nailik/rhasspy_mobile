package org.rhasspy.mobile.nativeutils

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class FileWriter(filename: String, maxFileSize: Long) {

    val maxFileSize: Long

    fun createFile(): Boolean

    fun appendText(element: String)

    fun getFileContent(): String

    fun getFileData(): List<Byte>

    fun writeData(byteData: List<Byte>)

    fun shareFile()

    fun saveFile(fileName: String, fileType: String)

}