package org.rhasspy.mobile.logic.nativeutils

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect open class FileWriter(filename: String) {

    /**
     * clears file content
     */
    fun clearFile()

}