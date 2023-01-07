package org.rhasspy.mobile.nativeutils

/**
 * write file with name and maximum size to app storage
 */
expect class FileWriter(filename: String, maxFileSize: Long) {

    val maxFileSize: Long

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
     * clears file content
     */
    fun clearFile()

    /**
     * read all file contents
     */
    fun getFileContent(): String

    /**
     * open share file system dialog
     */
    fun shareFile()

    /**
     * copy file to specific new file
     */
    fun copyFile(fileName: String, fileType: String)

}