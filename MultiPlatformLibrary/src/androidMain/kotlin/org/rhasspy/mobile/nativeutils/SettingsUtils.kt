package org.rhasspy.mobile.nativeutils

import android.app.Activity
import android.provider.OpenableColumns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.services.ServiceAction
import org.rhasspy.mobile.services.ServiceInterface
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


actual object SettingsUtils {


    actual fun saveSettingsFile() {
        File(Application.Instance.filesDir, "sounds").mkdirs()
        File(Application.Instance.filesDir, "porcupine").mkdirs()

        Application.Instance.currentActivity?.createDocument("rhasspy_settings_${Clock.System.now().toLocalDateTime(TimeZone.UTC)}.zip") {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->

                    Application.Instance.contentResolver.openOutputStream(uri)?.also { outputStream ->

                        val zipOutputStream = ZipOutputStream(BufferedOutputStream(outputStream))

                        //shared Prefs file
                        zipOutputStream.putNextEntry(ZipEntry("shared_prefs/"))

                        val sharedPreferencesFolder = File(Application.Instance.filesDir.parent, "shared_prefs")
                        if (sharedPreferencesFolder.exists()) {
                            sharedPreferencesFolder.listFiles()?.forEach { soundFile ->
                                zipOutputStream.putNextEntry(ZipEntry("${sharedPreferencesFolder.name}/${soundFile.name}"))
                                zipOutputStream.write(soundFile.readBytes())
                            }
                        }

                        //all files in sounds
                        val soundsFolder = File(Application.Instance.filesDir, "sounds")

                        if (soundsFolder.exists()) {
                            soundsFolder.listFiles()?.forEach { soundFile ->
                                zipOutputStream.putNextEntry(ZipEntry("files/${soundsFolder.name}/${soundFile.name}"))
                                zipOutputStream.write(soundFile.readBytes())
                            }
                        }

                        //all files in porcupine wake words
                        val porcupineFolder = File(Application.Instance.filesDir, "porcupine")

                        if (porcupineFolder.exists()) {
                            porcupineFolder.listFiles()?.forEach { porcupineFile ->
                                zipOutputStream.putNextEntry(ZipEntry("files/${porcupineFolder.name}/${porcupineFile.name}"))
                                zipOutputStream.write(porcupineFile.readBytes())
                            }
                        }

                        zipOutputStream.flush()
                        outputStream.flush()

                        zipOutputStream.close()
                        outputStream.close()
                    }
                }
            }
        }
    }

    actual fun restoreSettingsFromFile() {
        Application.Instance.currentActivity?.openDocument(arrayOf("application/zip")) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->

                    CoroutineScope(Dispatchers.Default).launch {
                        ServiceInterface.serviceAction(ServiceAction.Stop)
                    }

                    Application.Instance.contentResolver.openInputStream(uri)?.also { inputStream ->

                        val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))

                        var ze = zipInputStream.nextEntry

                        while (ze != null) {

                            if (ze.isDirectory) {
                                File(Application.Instance.filesDir.parent, ze.name).mkdirs()
                            } else {
                                val file = File(Application.Instance.filesDir.parent, ze.name)
                                File(file.parent!!).mkdirs()
                                file.createNewFile()
                                file.outputStream().apply {
                                    zipInputStream.copyTo(this)
                                    flush()
                                    close()
                                }
                            }
                            ze = zipInputStream.nextEntry
                        }

                        inputStream.close()

                        Application.Instance.restart()
                    }
                }
            }
        }
    }

    actual fun selectSoundFile(callback: (String?) -> Unit) {
        File(Application.Instance.filesDir, "sounds").mkdirs()

        Application.Instance.currentActivity?.openDocument(arrayOf("audio/x-wav")) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->

                    var fileName = ""

                    Application.Instance.contentResolver.query(uri, null, null, null, null)?.also { cursor ->
                        if (cursor.moveToFirst()) {
                            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (index != -1) {
                                fileName = cursor.getString(index)
                            } else {
                                callback(null)
                                //didn't work
                                return@openDocument
                            }
                        } else {
                            callback(null)
                            //didn't work
                            return@openDocument
                        }
                        cursor.close()
                    }

                    fileName = renameFileWhileExists("sounds", fileName)

                    Application.Instance.contentResolver.openInputStream(uri)?.let { inputStream ->
                        File(Application.Instance.filesDir, "sounds/$fileName").apply {
                            this.outputStream().apply {
                                inputStream.copyTo(this)

                                this.flush()

                                this.close()
                                inputStream.close()
                            }
                        }
                        callback(fileName)
                    } ?: run {
                        callback(null)
                    }
                }
            } else {
                callback(null)
            }
        }
    }

    actual fun removeSoundFile(fileName: String) {
        File(Application.Instance.filesDir, "sounds/$fileName").delete()
    }

    actual fun selectPorcupineFile(callback: (String?) -> Unit) {
        File(Application.Instance.filesDir, "porcupine").mkdirs()

        Application.Instance.currentActivity?.openDocument(arrayOf("application/octet-stream", "application/zip")) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->

                    var fileName = ""

                    Application.Instance.contentResolver.query(uri, null, null, null, null)?.also { cursor ->
                        if (cursor.moveToFirst()) {
                            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (index != -1) {
                                fileName = cursor.getString(index)
                            } else {
                                //didn't work
                                return@openDocument
                            }
                        } else {
                            //didn't work
                            return@openDocument
                        }
                        cursor.close()
                    }

                    Application.Instance.contentResolver.openInputStream(uri)?.let { inputStream ->

                        when {
                            fileName.endsWith(".zip") -> {

                                //check if file contains .ppn file
                                val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))

                                var ze = zipInputStream.nextEntry

                                while (ze != null) {

                                    if (!ze.isDirectory) {
                                        if (ze.name.endsWith(".ppn")) {
                                            fileName = renameFileWhileExists("porcupine", ze.name)

                                            File(Application.Instance.filesDir, "porcupine/$fileName").outputStream().apply {
                                                zipInputStream.copyTo(this)
                                                flush()
                                                close()
                                            }
                                            callback(fileName)
                                            inputStream.close()
                                            return@openDocument
                                        }
                                    }
                                    ze = zipInputStream.nextEntry
                                }

                                inputStream.close()

                                callback(null)
                            }
                            fileName.endsWith(".ppn") -> {
                                //use this file
                                fileName = renameFileWhileExists("porcupine", fileName)

                                File(Application.Instance.filesDir, "porcupine/$fileName").apply {
                                    this.outputStream().apply {
                                        inputStream.copyTo(this)

                                        this.flush()

                                        this.close()
                                        inputStream.close()
                                    }
                                }
                                callback(fileName)
                            }
                            else -> callback(null)
                        }

                    } ?: run {
                        //didn't work
                        println("err")
                    }

                }
            } else {
                callback(null)
            }
        }
    }

    //rename file while it already exists
    private fun renameFileWhileExists(folder: String, file: String): String {
        var fileName = file
        var index = 0
        while (File(Application.Instance.filesDir, "$folder/$fileName").exists()) {
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