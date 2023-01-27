package org.rhasspy.mobile.logic.nativeutils

expect open class FileWriter(filename: String) {

    /**
     * clears file content
     */
    fun clearFile()

}