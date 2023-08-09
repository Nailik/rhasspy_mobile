package org.rhasspy.mobile.platformspecific.file

import dev.icerock.moko.resources.FileResource
import kotlinx.io.files.Path

/**
 * to open file selection or delete file from local app storage
 */
expect object FileUtils {

    /**
     * open file selection and copy file to specific folder and return selected file name
     */
    suspend fun selectPath(folderType: FolderType): Path?

    fun getFilePath(fileName: String): String

    fun getPath(fileName: String): Path

    fun getSize(fileName: String): Long

    fun shareFile(fileName: String): Boolean

    suspend fun exportFile(path: Path, fileName: String, fileType: String): Boolean

    fun commonData(resource: FileResource): ByteArray

}