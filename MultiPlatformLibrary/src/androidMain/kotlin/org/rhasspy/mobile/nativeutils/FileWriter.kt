package org.rhasspy.mobile.nativeutils

import org.rhasspy.mobile.Application
import java.io.File

actual open class FileWriter actual constructor(filename: String) {

    //file to write to
    protected open val file = File(Application.nativeInstance.filesDir, filename)

    /**
     * clears file content
     */
    actual fun clearFile() {
        file.writeBytes(ByteArray(0))
    }

    /**
     * length of content
     */
    actual fun length(): Long {
        return file.length()
    }

}