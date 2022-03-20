package org.rhasspy.mobile.services.native

actual class FileWriter actual constructor(filename: String, maxFileSize: Long) {

    actual val maxFileSize: Long
        get() = TODO("Not yet implemented")

    actual fun createFile(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun appendText(element: String) {
        TODO("Not yet implemented")
    }

    actual fun getFileContent(): String {
        TODO("Not yet implemented")
    }

    actual fun appendData(byteData: List<Byte>) {
        TODO("Not yet implemented")
    }

}