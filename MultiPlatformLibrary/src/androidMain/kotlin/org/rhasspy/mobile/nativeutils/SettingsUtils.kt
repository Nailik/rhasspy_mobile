package org.rhasspy.mobile.nativeutils

import android.app.Activity
import android.provider.OpenableColumns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
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
        File(Application.Instance.filesDir.parent, "sounds").mkdirs()

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
                        zipOutputStream.putNextEntry(ZipEntry("sounds/"))

                        val soundsFolder = File(Application.Instance.filesDir.parent, "sounds")

                        if (soundsFolder.exists()) {
                            soundsFolder.listFiles()?.forEach { soundFile ->
                                zipOutputStream.putNextEntry(ZipEntry("${soundsFolder.name}/${soundFile.name}"))
                                zipOutputStream.write(soundFile.readBytes())
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

    @OptIn(ExperimentalSerializationApi::class)
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
                                File(Application.Instance.filesDir.parent, ze.name).outputStream().apply {
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

    actual fun selectSoundFile(callback: (String) -> Unit) {

        File(Application.Instance.filesDir.parent, "sounds").mkdirs()

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
                        File(Application.Instance.filesDir.parent, "sounds/$fileName").apply {
                            this.outputStream().apply {
                                inputStream.copyTo(this)

                                this.flush()

                                this.close()
                                inputStream.close()
                            }
                        }
                        callback(fileName)
                    } ?: kotlin.run {
                        //didn't work
                    }

                }
            } else {
                //didn't work
            }
        }
    }

}