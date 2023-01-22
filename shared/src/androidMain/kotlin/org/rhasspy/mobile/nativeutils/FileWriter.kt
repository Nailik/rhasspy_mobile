package org.rhasspy.mobile.nativeutils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import org.rhasspy.mobile.Application
import java.io.File

/**
 * write file with name and maximum size to app storage
 */
actual class FileWriter actual constructor(filename: String, actual val maxFileSize: Long) {
    //file to write to
    private val file = File(Application.nativeInstance.filesDir, filename)

    /**
     * create file and return if it was successfully
     */
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

        if (maxFileSize != 0L) {
            if (file.length() / 1024 >= maxFileSize) {
                try {
                    val oldFile =
                        File("${file.parent}/${file.nameWithoutExtension}_old.${file.extension}")
                    if (oldFile.exists()) {
                        //overwrite old file content
                        oldFile.writeText(file.readText())
                    } else {
                        //rename current file to new
                        file.renameTo(oldFile)
                        //clear current file
                        file.writeText("")
                    }
                } catch (e: Exception) {
                    //don't log else recursive issue
                    println(e)
                }
            }
        }
    }

    /**
     * clears file content
     */
    actual fun clearFile() {
        file.writeText("")
    }

    /**
     * read all file contents
     */
    actual fun getFileContent() = file.readText()

    /**
     * open share file system dialog
     */
    actual fun shareFile() {
        val fileUri: Uri = FileProvider.getUriForFile(
            Application.nativeInstance,
            Application.nativeInstance.packageName.toString() + ".provider",
            file
        )

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = "text/html"
        }
        Application.nativeInstance.startActivity(Intent.createChooser(shareIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    /**
     * copy file to specific new file
     */
    actual fun copyFile(fileName: String, fileType: String) {
        Application.nativeInstance.currentActivity?.createDocument(fileName, fileType) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->
                    Application.nativeInstance.contentResolver.openOutputStream(uri)
                        ?.also { outputStream ->
                            outputStream.write(file.readBytes())
                            outputStream.flush()
                            outputStream.close()
                        }
                }
            }
        }
    }

}