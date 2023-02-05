package org.rhasspy.mobile.logic.nativeutils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.koin.core.component.get
import java.io.File

/**
 * write file with name and maximum size to app storage
 */
actual class FileWriterText actual constructor(filename: String, actual val maxFileSize: Long?) : FileWriter(filename) {

    private val context = get<NativeApplication>()

    actual fun createFile(): Boolean {
        if (!file.exists()) {
            file.createNewFile()
            return true
        }
        return false
    }

    /**
     * append text to the file if not bigger than max file size
     * if max file size is reached this file will be copied to filename_old
     * and a new file is created
     */
    actual fun appendText(element: String) {
        file.appendText("\n$element")

        maxFileSize?.also {
            if (file.length() / 1024 >= it) {
                try {
                    val oldFile = File("${file.parent}/${file.nameWithoutExtension}_old.${file.extension}")
                    if (oldFile.exists()) {
                        //overwrite old file content
                        file.copyTo(oldFile, true)
                        file.writeBytes(ByteArray(0))
                    }
                } catch (e: Exception) {
                    //don't log else recursive issue
                    println(e)
                }
            }
        }
    }

    /**
     * read all file contents
     */
    @OptIn(ExperimentalSerializationApi::class)
    actual inline fun <reified T> decodeFromFile(): T {
        return Json.decodeFromStream(file.inputStream())
    }

    /**
     * open share file system dialog
     */
    actual fun shareFile() {
        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            context.packageName.toString() + ".provider",
            file
        )

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = "text/html"
        }
        context.startActivity(Intent.createChooser(shareIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    /**
     * copy file to specific new file
     */
    actual fun copyFile(fileName: String, fileType: String) {
        context.currentActivity?.createDocument(fileName, fileType) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->
                    context.contentResolver.openOutputStream(uri)
                        ?.also { outputStream ->
                            file.inputStream().copyTo(outputStream)
                            outputStream.flush()
                            outputStream.close()
                        }
                }
            }
        }
    }

}