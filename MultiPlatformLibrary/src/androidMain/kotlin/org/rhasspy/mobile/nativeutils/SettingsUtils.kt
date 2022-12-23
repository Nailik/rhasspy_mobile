package org.rhasspy.mobile.nativeutils

import android.app.Activity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.rhasspy.mobile.Application
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

        Application.Instance.currentActivity?.createDocument(
            "rhasspy_settings_${Clock.System.now().toLocalDateTime(TimeZone.UTC)}.zip",
            "application/zip"
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->

                    Application.Instance.contentResolver.openOutputStream(uri)
                        ?.also { outputStream ->

                            val zipOutputStream =
                                ZipOutputStream(BufferedOutputStream(outputStream))

                            //shared Prefs file
                            zipOutputStream.putNextEntry(ZipEntry("shared_prefs/"))

                            val sharedPreferencesFolder =
                                File(Application.Instance.filesDir.parent, "shared_prefs")
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

                    TODO("reinitialize koin di")
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
}