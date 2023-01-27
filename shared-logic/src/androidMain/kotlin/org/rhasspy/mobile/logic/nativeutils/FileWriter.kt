package org.rhasspy.mobile.logic.nativeutils

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.File

/**
 * write file with name and maximum size to app storage
 */
actual open class FileWriter actual constructor(filename: String) : KoinComponent {

    private val context = get<NativeApplication>()

    //file to write to
    protected open val file = File(context.filesDir, filename)

    /**
     * clears file content
     */
    actual fun clearFile() {
        file.writeBytes(ByteArray(0))
    }

}