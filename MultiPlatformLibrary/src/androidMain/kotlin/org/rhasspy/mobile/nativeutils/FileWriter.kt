package org.rhasspy.mobile.nativeutils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import org.rhasspy.mobile.Application
import java.io.File


actual class FileWriter actual constructor(filename: String, actual val maxFileSize: Long) {

    private val file = File(Application.Instance.filesDir, filename)

    actual fun createFile(): Boolean {
        if (!file.exists()) {
            file.createNewFile()
            return true
        }
        return false
    }

    actual fun appendText(element: String) {
        file.appendText("\n$element")

        if (maxFileSize != 0L) {
            if (file.length() / 1024 >= maxFileSize) {
                try {
                    val oldFile = File("${file.parent}/${file.nameWithoutExtension}_old.${file.extension}")
                    if (oldFile.exists()) {
                        oldFile.delete()
                    }
                    file.copyTo(oldFile)
                } catch (e: Exception) {

                }
            }
        }
    }

    actual fun getFileContent() = file.readText()

    actual fun writeData(byteData: List<Byte>) {
        file.writeBytes(byteData.toByteArray())
    }

    actual fun getFileData() = file.readBytes().toList()

    actual fun shareFile() {
        val fileUri: Uri = FileProvider.getUriForFile(Application.Instance, Application.Instance.packageName.toString() + ".provider", file)

        val intent = ShareCompat.IntentBuilder(Application.Instance)
            .setStream(fileUri) // uri from FileProvider
            .setType("text/html")
            .intent
            .setAction(Intent.ACTION_SEND) //Change if needed
            .setDataAndType(fileUri, "text/*")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Application.Instance.startActivity(intent)
    }

    actual fun saveFile(fileName: String) {
        Application.Instance.currentActivity?.createDocument(fileName) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->
                    Application.Instance.contentResolver.openOutputStream(uri)?.also { outputStream ->
                        outputStream.write(file.readBytes())
                        outputStream.flush()
                        outputStream.close()
                    }
                }
            }
        }
    }

}