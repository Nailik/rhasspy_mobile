package org.rhasspy.mobile.nativeutils

import org.rhasspy.mobile.settings.types.FileType

/**
 * to open file selection or delete file from local app storage
 */
expect object FileUtils {

    /**
     * open file selection and copy file to specific folder and return selected file name
     */
    suspend fun selectFile(fileType: FileType, subfolder: String? = null): String?

    /**
     * delete file from local app storage
     */
    fun removeFile(fileType: FileType, subfolder: String? = null, fileName: String)

}