package org.rhasspy.mobile.nativeutils

import android.net.Uri
import android.provider.OpenableColumns
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.settings.types.FileType
import java.io.BufferedInputStream
import java.io.File
import java.util.zip.ZipInputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * to open file selection or delete file from local app storage
 */
actual object FileUtils {

    /**
     * open file selection and copy file to specific folder and return selected file name
     */
    actual suspend fun selectFile(
        fileType: FileType,
        subfolder: String?
    ): String? {
        //create folder if it doesn't exist yet
        val folderName = "${fileType.folderName}${subfolder?.let { "/$it" } ?: ""}"
        File(Application.nativeInstance.filesDir, folderName).mkdirs()

        openDocument(fileType)?.also { uri ->
            queryFile(uri)?.also { fileName ->
                val finalFileName = renameFileWhileExists(folderName, fileName)
                return copyFile(fileType, uri, folderName, finalFileName)
            }
        }
        return null
    }


    /**
     * delete file from local app storage
     */
    actual fun removeFile(fileType: FileType, subfolder: String?, fileName: String) {
        val folderName = "${fileType.folderName}${subfolder?.let { "/$it" } ?: ""}"
        File(Application.nativeInstance.filesDir, "$folderName/$fileName").delete()
    }


    /**
     * read file from system
     */
    private suspend fun queryFile(uri: Uri): String? = suspendCoroutine { continuation ->
        Application.nativeInstance.contentResolver.query(uri, null, null, null, null)?.also { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    //file found
                    val fileName = cursor.getString(index)
                    cursor.close()
                    continuation.resume(fileName)
                    return@suspendCoroutine
                }
            }
            //didn't work
            cursor.close()
            continuation.resume(null)
            return@suspendCoroutine
        }
    }

    /**
     * open document
     */
    private suspend fun openDocument(fileType: FileType): Uri? = suspendCoroutine { continuation ->
        Application.nativeInstance.currentActivity?.openDocument(fileType.fileTypes) {
            continuation.resume(it.data?.data)
        }
    }

    /**
     * copy file to destination folder
     */
    private suspend fun copyFile(
        fileType: FileType,
        uri: Uri,
        folderName: String,
        fileName: String
    ): String? =
        when (fileType) {
            FileType.PORCUPINE -> copyPorcupineFile(uri, folderName, fileName)
            else -> copyNormalFile(uri, folderName, fileName)
        }

    /**
     * open normal file and copy
     */
    private suspend fun copyNormalFile(uri: Uri, folderName: String, fileName: String): String? =
        suspendCoroutine { continuation ->
            Application.nativeInstance.contentResolver.openInputStream(uri)?.also { inputStream ->
                File(Application.nativeInstance.filesDir, "$folderName/$fileName").apply {
                    this.outputStream().apply {
                        inputStream.copyTo(this)

                        this.flush()

                        this.close()
                        inputStream.close()
                    }
                }
                continuation.resume(fileName)
            } ?: run {
                continuation.resume(null)
            }
        }

    /**
     * open porcupine file and copy
     */
    private suspend fun copyPorcupineFile(
        uri: Uri,
        folderName: String,
        selectedFileName: String
    ): String? =
        suspendCoroutine { continuation ->
            Application.nativeInstance.contentResolver.openInputStream(uri)?.also { inputStream ->

                when {
                    selectedFileName.endsWith(".zip") -> {

                        //check if file contains .ppn file
                        val zipInputStream =
                            ZipInputStream(BufferedInputStream(inputStream))

                        var ze = zipInputStream.nextEntry

                        while (ze != null) {

                            if (!ze.isDirectory) {
                                if (ze.name.endsWith(".ppn")) {
                                    val fileName = renameFileWhileExists(folderName, ze.name)

                                    File(
                                        Application.nativeInstance.filesDir,
                                        "$folderName/$fileName"
                                    ).outputStream().apply {
                                        zipInputStream.copyTo(this)
                                        flush()
                                        close()
                                    }
                                    inputStream.close()
                                    continuation.resume(fileName)
                                    return@suspendCoroutine
                                }
                            }
                            ze = zipInputStream.nextEntry
                        }

                        inputStream.close()
                    }
                    selectedFileName.endsWith(".ppn") -> {
                        //use this file
                        val fileName = renameFileWhileExists(folderName, selectedFileName)

                        File(Application.nativeInstance.filesDir, "$folderName/$fileName").apply {
                            this.outputStream().apply {
                                inputStream.copyTo(this)
                                this.flush()
                                this.close()
                                inputStream.close()
                            }
                        }
                        continuation.resume(fileName)
                        return@suspendCoroutine
                    }
                }
            }

            //whgen not yet returned, then there is an issue
            continuation.resume(null)
        }

    /**
     *  rename file while it already exists
     */
    private fun renameFileWhileExists(folder: String, file: String): String {
        var fileName = file
        var index = 0
        while (File(Application.nativeInstance.filesDir, "$folder/$fileName").exists()) {
            index++
            fileName = if (fileName.contains(Regex("\\([1-9]+\\)."))) {
                fileName.replace(Regex("\\([1-9]+\\)."), "($index).")
            } else {
                "${fileName.substringBeforeLast(".")}($index).${fileName.substringAfterLast(".")}"
            }
        }
        return fileName
    }

}