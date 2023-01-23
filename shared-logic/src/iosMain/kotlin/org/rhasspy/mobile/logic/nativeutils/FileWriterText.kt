package org.rhasspy.mobile.logic.nativeutils

actual class FileWriterText actual constructor(filename: String, maxFileSize: Long?) : FileWriter(filename) {

    actual val maxFileSize: Long?
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