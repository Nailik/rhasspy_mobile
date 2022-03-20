package org.rhasspy.mobile.services.native

import org.rhasspy.mobile.Application
import java.io.File

actual class FileWriter actual constructor(filename: String, actual val maxFileSize: Long) {

    private val file = File(Application.Instance.filesDir, filename)

    actual fun createFile(): Boolean {
        if (!file.exists()) {
            file.createNewFile()
            return true
        }
        return false
    }

    actual fun appendText(element: String) {
        file.appendText(",\n$element")

        if (maxFileSize != 0L) {
            if (file.length() / 1024 >= maxFileSize) {
                try {
                    val oldFile = File("${file.parent}/${file.nameWithoutExtension}_old.${file.extension}")
                    if (oldFile.exists()) {
                        oldFile.delete()
                    }
                    file.copyTo(oldFile)
                } catch (e: Exception) {

                }
            }
        }
    }

    actual fun getFileContent() = file.readText()

    actual fun appendData(byteData: List<Byte>) {
        file.appendBytes(byteData.toByteArray())
    }

}