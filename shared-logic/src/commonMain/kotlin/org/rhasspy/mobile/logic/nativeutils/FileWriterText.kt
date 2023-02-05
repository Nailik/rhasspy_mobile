package org.rhasspy.mobile.logic.nativeutils

/**
 * write file with name and maximum size to app storage
 */
expect class FileWriterText(filename: String, maxFileSize: Long? = null) : FileWriter {

    val maxFileSize: Long?

    /**
     * create file and return if it was successfully
     */
    fun createFile(): Boolean

    /**
     * append text to the file if not bigger than max file size
     * if max file size is reached this file will be copied to filename_old
     * and a new file is created
     */
    fun appendText(element: String)

    /**
     * read all file contents
     */
    inline fun <reified T> decodeFromFile(): T

    /**
     * open share file system dialog
     */
    fun shareFile()

    /**
     * copy file to specific new file
     */
    fun copyFile(fileName: String, fileType: String)

}