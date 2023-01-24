package org.rhasspy.mobile.logic.nativeutils

import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.logic.fileutils.FolderType

/**
 * to open file selection or delete file from local app storage
 */
expect object FileUtils {

    /**
     * open file selection and copy file to specific folder and return selected file name
     */
    suspend fun selectFile(folderType: FolderType): String?

    /**
     * read data from file
     */
    fun readDataFromFile(fileResource: FileResource): ByteArray

    /**
     * delete file from local app storage
     */
    fun removeFile(folderType: FolderType, fileName: String)

}