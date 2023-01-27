package org.rhasspy.mobile.logic.nativeutils

actual open class FileWriter actual constructor(val filename: String) {
    /**
     * clears file content
     */
    actual fun clearFile() {
    }
}