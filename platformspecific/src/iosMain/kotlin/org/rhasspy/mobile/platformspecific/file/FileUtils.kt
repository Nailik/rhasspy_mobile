package org.rhasspy.mobile.platformspecific.file

import dev.icerock.moko.resources.FileResource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask


/**
 * to open file selection or delete file from local app storage
 */
actual object FileUtils {
    /**
     * open file selection and copy file to specific folder and return selected file name
     */
    actual suspend fun selectPath(folderType: FolderType): Path? {
        return null
    }

    actual fun getFilePath(fileName: String): String {
        return "${readDocumentsDirectory().replace("file:", "")}/$fileName"
    }

    actual fun getPath(fileName: String): Path {
        return Path(getFilePath(fileName))
    }

    actual fun getSize(fileName: String): Long {
        //TODO("Not yet implemented")
        return 0
    }

    actual fun shareFile(fileName: String): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    actual suspend fun exportFile(path: Path, fileName: String, fileType: String): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    actual fun commonData(resource: FileResource): ByteArray {
        //TODO("Not yet implemented")
        return ByteArray(0)
    }

    private val fileManager = NSFileManager.defaultManager

    @OptIn(ExperimentalForeignApi::class)
    private fun readDocumentsDirectory(): String {
        val documentsDirectoryUrl = fileManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )!!

        createDirectoryIfNotExists(documentsDirectoryUrl)
        return documentsDirectoryUrl.absoluteString!!
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun createDirectoryIfNotExists(parentDirectory: NSURL) {
        if (!fileManager.fileExistsAtPath(parentDirectory.path!!)) {
            fileManager.createDirectoryAtPath(parentDirectory.path!!, true, null, null)
        }
    }


}