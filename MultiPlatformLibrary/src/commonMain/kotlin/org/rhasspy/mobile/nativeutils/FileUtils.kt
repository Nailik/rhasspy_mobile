package org.rhasspy.mobile.nativeutils

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.settings.types.FileType

/**
 * to open file selection or delete file from local app storage
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object FileUtils {

    /**
     * open file selection and copy file to specific folder and return selected file name
     */
    suspend fun selectFile(fileType: FileType, subfolder: String? = null): String?

    /**
     * read data from file
     */
    fun readDataFromFile(fileResource: FileResource): ByteArray

    /**
     * delete file from local app storage
     */
    fun removeFile(fileType: FileType, subfolder: String? = null, fileName: String)

}