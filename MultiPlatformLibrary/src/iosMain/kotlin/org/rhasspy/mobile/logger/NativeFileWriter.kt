package org.rhasspy.mobile.logger

actual class NativeFileWriter actual constructor(filename: String) {

    actual fun appendJsonElement(element: String) {

    }

    actual fun getFileContent(): String {
        return ""
    }

}