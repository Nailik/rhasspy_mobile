package org.rhasspy.mobile.nativeutils

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

    actual fun writeData(byteData: List<Byte>) {
        TODO("Not yet implemented")
    }

    actual fun getFileData(): List<Byte> {
        TODO("Not yet implemented")
    }

}