package org.rhasspy.mobile.nativeutils

import android.net.Uri
import android.provider.OpenableColumns
import dev.icerock.moko.resources.FileResource
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.fileutils.FolderType
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
    actual suspend fun selectFile(folderType: FolderType): String? {
        //create folder if it doesn't exist yet
        File(Application.nativeInstance.filesDir, folderType.toString()).mkdirs()

        openDocument(folderType)?.also { uri ->
            queryFile(uri)?.also { fileName ->
                val finalFileName = renameFileWhileExists(folderType.toString(), fileName)
                return copyFile(folderType, uri, folderType.toString(), finalFileName)
            }
        }
        return null
    }

    /**
     * read data from file
     */
    actual fun readDataFromFile(fileResource: FileResource): ByteArray {
        return Application.nativeInstance.resources.openRawResource(fileResource.rawResId)
            .readBytes()
    }

    /**
     * delete file from local app storage
     */
    actual fun removeFile(folderType: FolderType, fileName: String) {
        File(Application.nativeInstance.filesDir, "$folderType/$fileName").delete()
    }


    /**
     * read file from system
     */
    private suspend fun queryFile(uri: Uri): String? = suspendCoroutine { continuation ->
        Application.nativeInstance.contentResolver.query(uri, null, null, null, null)
            ?.also { cursor ->
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
    private suspend fun openDocument(folderType: FolderType): Uri? =
        suspendCoroutine { continuation ->
            Application.nativeInstance.currentActivity?.openDocument(folderType.fileTypes) {
                continuation.resume(it.data?.data)
            }
        }

    /**
     * copy file to destination folder
     */
    private suspend fun copyFile(
        folderType: FolderType,
        uri: Uri,
        folderName: String,
        fileName: String
    ): String? =
        when (folderType) {
            FolderType.PorcupineFolder -> copyPorcupineFile(uri, folderName, fileName)
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