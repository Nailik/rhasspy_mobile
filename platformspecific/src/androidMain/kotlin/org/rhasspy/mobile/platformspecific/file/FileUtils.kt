package org.rhasspy.mobile.platformspecific.file

import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import co.touchlab.kermit.Severity
import dev.icerock.moko.resources.FileResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.io.asInputStream
import kotlinx.io.files.Path
import kotlinx.io.files.source
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectUtils
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import org.rhasspy.mobile.platformspecific.resumeSave
import java.io.*
import java.util.Collections
import java.util.zip.ZipInputStream

/**
 * to open file selection or delete file from local app storage
 */
actual object FileUtils : KoinComponent {

    private val nativeApplication = get<NativeApplication>()
    private val externalResultRequest = get<IExternalResultRequest>()

    actual fun getFilePath(fileName: String): String {
        return "${nativeApplication.filesDir?.let { "$it/" } ?: ""}$this"
    }

    actual fun getPath(fileName: String): Path {
        return Path(getFilePath(fileName))
    }

    actual fun getSize(fileName: String): Long {
        return File(getFilePath(fileName)).length()
    }

    actual fun shareFile(fileName: String): Boolean {
        //copy file
        val fileUri: Uri = FileProvider.getUriForFile(
            nativeApplication, nativeApplication.packageName.toString() + ".provider",
            File(fileName)
        )

        val result = externalResultRequest.launch(
            ExternalResultRequestIntention.ShareFile(
                fileUri = fileUri.toString(),
                mimeType = "text/html"
            )
        )

        return result is ExternalRedirectResult.Success
    }

    actual suspend fun exportFile(path: Path, fileName: String, fileType: String): Boolean {

        val result = externalResultRequest.launchForResult(
            ExternalResultRequestIntention.CreateDocument(
                fileName,
                fileType
            )
        )

        return if (result is ExternalRedirectResult.Result) {
            nativeApplication.contentResolver.openOutputStream(result.data.toUri())
                ?.also { outputStream ->
                    path.source().asInputStream().copyTo(outputStream)
                    outputStream.flush()
                    outputStream.close()
                }

            true
        } else false
    }

    /**
     * open file selection and copy file to specific folder and return selected file name
     */
    actual suspend fun selectPath(folderType: FolderType): Path? =
        suspendCancellableCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                ExternalRedirectUtils.openDocument(
                    folder = SystemFolderType.Download.folder,
                    folderType = folderType
                )?.also { path ->
                    val uri = path.toUri()

                    queryFile(uri)?.also { fileName ->
                        val finalFileName =
                            renameFileWhileExists(nativeApplication.filesDir, folderType.toString(), fileName)
                        //create folder if it doesn't exist yet
                        File(nativeApplication.filesDir, folderType.toString()).mkdirs()
                        val result = copyFile(folderType, uri, folderType.toString(), finalFileName)
                        continuation.resumeSave(result?.let { Path(it) })
                    } ?: run {
                        continuation.resumeSave(null)
                    }

                } ?: run {
                    continuation.resumeSave(null)
                }
            }
        }

    actual fun commonData(resource: FileResource): ByteArray =
        nativeApplication.resources.openRawResource(resource.rawResId).readBytes()

    /**
     * read file from system
     */
    private suspend fun queryFile(uri: Uri): String? = suspendCancellableCoroutine { continuation ->
        nativeApplication.contentResolver.query(uri, null, null, null, null)
            ?.also { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        //file found
                        val fileName = cursor.getString(index)
                        cursor.close()
                        continuation.resumeSave(fileName)
                        return@suspendCancellableCoroutine
                    }
                }
                //didn't work
                cursor.close()
                continuation.resumeSave(null)
                return@suspendCancellableCoroutine
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
            else                       -> copyNormalFile(uri, folderName, fileName)
        }


    /**
     * open normal file and copy
     */
    private suspend fun copyNormalFile(uri: Uri, folderName: String, fileName: String): String? =
        suspendCancellableCoroutine { continuation ->
            nativeApplication.contentResolver.openInputStream(uri)?.also { inputStream ->
                File(nativeApplication.filesDir, "$folderName/$fileName").apply {
                    this.outputStream().apply {
                        inputStream.copyTo(this)

                        this.flush()

                        this.close()
                        inputStream.close()
                    }
                }
                continuation.resumeSave(fileName)
            } ?: run {
                continuation.resumeSave(null)
            }
        }

    /**
     * open porcupine file and copy
     */
    private suspend fun copyPorcupineFile(
        uri: Uri,
        folderName: String,
        selectedFileName: String
    ): String? = suspendCancellableCoroutine { continuation ->
        nativeApplication.contentResolver.openInputStream(uri)?.also { inputStream ->

            when {
                selectedFileName.endsWith(".zip") -> {

                    //check if file contains .ppn file
                    val zipInputStream =
                        ZipInputStream(BufferedInputStream(inputStream))

                    var ze = zipInputStream.nextEntry

                    while (ze != null) {

                        if (!ze.isDirectory) {
                            if (ze.name.endsWith(".ppn")) {
                                val fileName =
                                    renameFileWhileExists(nativeApplication.filesDir, folderName, ze.name)

                                File(
                                    nativeApplication.filesDir,
                                    "$folderName/$fileName"
                                ).outputStream().apply {
                                    zipInputStream.copyTo(this)
                                    flush()
                                    close()
                                }
                                inputStream.close()
                                continuation.resumeSave(fileName)
                                return@suspendCancellableCoroutine
                            }
                        }
                        ze = zipInputStream.nextEntry
                    }

                    inputStream.close()
                }

                selectedFileName.endsWith(".ppn") -> {
                    //use this file
                    val fileName =
                        renameFileWhileExists(nativeApplication.filesDir, folderName, selectedFileName)

                    File(nativeApplication.filesDir, "$folderName/$fileName").apply {
                        this.outputStream().apply {
                            inputStream.copyTo(this)
                            this.flush()
                            this.close()
                            inputStream.close()
                        }
                    }
                    continuation.resumeSave(fileName)
                    return@suspendCancellableCoroutine
                }
            }
        }

        //when not yet returned, then there is an issue
        continuation.resumeSave(null)
    }

    /**
     *  rename file while it already exists
     */
    private fun renameFileWhileExists(dir: File, folder: String, file: String): String {
        var fileName = file
        var index = 0
        while (File(dir, "$folder/$fileName").exists()) {
            index++
            fileName = if (fileName.contains(Regex("\\([1-9]+\\)."))) {
                fileName.replace(Regex("\\([1-9]+\\)."), "($index).")
            } else {
                "${fileName.substringBeforeLast(".")}($index).${fileName.substringAfterLast(".")}"
            }
        }
        return fileName
    }

    fun InputStream.modify(): InputStream {
        val streams = listOf(
            ByteArrayInputStream(
                "[${
                    Json.encodeToString(
                        LogElement(
                            time = "",
                            severity = Severity.Assert,
                            tag = "",
                            message = "",
                            throwable = null
                        )
                    )
                }".toByteArray()
            ),
            this,
            ByteArrayInputStream("]".toByteArray())
        )
        return SequenceInputStream(Collections.enumeration(streams))
    }

}