package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.settings.types.FileType

/**
 * to open file selection or delete file from local app storage
 */
actual object FileUtils {
    /**
     * open file selection and copy file to specific folder and return selected file name
     */
    actual suspend fun selectFile(fileType: FileType, subfolder: String?): String? {
        TODO("Not yet implemented")
    }

    /**
     * read data from file
     */
    actual fun readDataFromFile(fileResource: FileResource): ByteArray {
        TODO("Not yet implemented")
    }

    /**
     * delete file from local app storage
     */
    actual fun removeFile(fileType: FileType, subfolder: String?, fileName: String) {
    }
}