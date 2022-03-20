package org.rhasspy.mobile.services.native

expect class FileWriter(filename: String, maxFileSize: Long) {

    val maxFileSize: Long

    fun createFile() : Boolean

    fun appendText(element: String)

    fun getFileContent(): String

    fun appendData(byteData: List<Byte>)

}