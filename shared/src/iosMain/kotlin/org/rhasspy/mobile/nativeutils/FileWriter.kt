package org.rhasspy.mobile.nativeutils

actual class FileWriter actual constructor(filename: String, maxFileSize: Long) {

    actual val maxFileSize: Long
        get() = TODO("Not yet implemented")

    /**
     * create file and return if it was successfully
     */
    actual fun createFile(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * append text to the file if not bigger than max file size
     * if max file size is reached this file will be copied to filename_old
     * and a new file is created
     */
    actual fun appendText(element: String) {
    }

    /**
     * clears file content
     */
    actual fun clearFile() {
    }

    /**
     * read all file contents
     */
    actual fun getFileContent(): String {
        TODO("Not yet implemented")
    }

    /**
     * open share file system dialog
     */
    actual fun shareFile() {
    }

    /**
     * copy file to specific new file
     */
    actual fun copyFile(fileName: String, fileType: String) {
    }

}