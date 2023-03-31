package org.rhasspy.mobile.logic.nativeutils

import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.fileutils.FolderType
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import java.io.BufferedInputStream
import java.io.File
import java.util.zip.ZipInputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * to open file selection or delete file from local app storage
 */
actual object FileUtils : KoinComponent {

    private val context = get<NativeApplication>()

    /**
     * open file selection and copy file to specific folder and return selected file name
     */
    actual suspend fun selectFile(folderType: FolderType): Path? =
        suspendCoroutine { continuation ->
            context.currentActivity?.openDocument(folderType.fileTypes) {
                CoroutineScope(Dispatchers.IO).launch {

                    it.data?.data?.also { uri ->

                        queryFile(uri)?.also { fileName ->
                            //create folder if it doesn't exist yet
                            File(context.filesDir, folderType.toString()).mkdirs()
                            val result = copyFile(folderType, uri, folderType.toString(), fileName)
                            continuation.resume(result?.toPath())

                        } ?: kotlin.run {
                            continuation.resume(null)
                        }

                    } ?: kotlin.run {
                        continuation.resume(null)
                    }
                }
            }
        }


    /**
     * read file from system
     */
    private suspend fun queryFile(uri: Uri): String? = suspendCoroutine { continuation ->
        context.contentResolver.query(uri, null, null, null, null)
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
            context.contentResolver.openInputStream(uri)?.also { inputStream ->
                File(context.filesDir, "$folderName/$fileName").apply {
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
        fileName: String
    ): String? =
        suspendCoroutine { continuation ->
            context.contentResolver.openInputStream(uri)?.also { inputStream ->

                when {
                    fileName.endsWith(".zip") -> {

                        //check if file contains .ppn file
                        val zipInputStream =
                            ZipInputStream(BufferedInputStream(inputStream))

                        var ze = zipInputStream.nextEntry

                        while (ze != null) {

                            if (!ze.isDirectory) {
                                if (ze.name.endsWith(".ppn")) {
                                    File(
                                        context.filesDir,
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

                    fileName.endsWith(".ppn") -> {
                        //use this file
                        File(context.filesDir, "$folderName/$fileName").apply {
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

            //when not yet returned, then there is an issue
            continuation.resume(null)
        }

}