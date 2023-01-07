package org.rhasspy.mobile.nativeutils

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.settings.SettingsEnum
import org.rhasspy.mobile.settings.types.FileType
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


actual object SettingsUtils {

    /**
     * export the settings file
     */
    actual fun exportSettingsFile() {
        //create folder for sounds and porcupine
        File(Application.nativeInstance.filesDir, "sounds").mkdirs()
        File(Application.nativeInstance.filesDir, "porcupine").mkdirs()

        //to load zip export file
        Application.nativeInstance.currentActivity?.createDocument(
            "rhasspy_settings_${Clock.System.now().toLocalDateTime(TimeZone.UTC)}.zip",
            "application/zip"
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->
                    Application.nativeInstance.contentResolver.openOutputStream(uri)
                        ?.also { outputStream ->

                            //create output for zip file
                            val zipOutputStream = ZipOutputStream(BufferedOutputStream(outputStream))

                            //shared Prefs file
                            zipOutputStream.putNextEntry(ZipEntry("shared_prefs/"))

                            //copy org.rhasspy.mobile.android_prefenrences.xml
                            val sharedPreferencesFile = File(Application.nativeInstance.filesDir.parent, "shared_prefs/org.rhasspy.mobile.android_preferences.xml")
                            if (sharedPreferencesFile.exists()) {
                                sharedPreferencesFile.listFiles()?.forEach { soundFile ->
                                    zipOutputStream.putNextEntry(ZipEntry("${sharedPreferencesFile.name}/${soundFile.name}"))
                                    zipOutputStream.write(soundFile.readBytes())
                                }
                            }

                            //all custom files
                            val files = File(Application.nativeInstance.filesDir, "files")
                            FileType.values().forEach { fileType ->
                                val fileTypeFolder = File(files, fileType.folderName)
                                copyFolderIntoZipRecursive(fileTypeFolder, zipOutputStream)
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

    /**
     * copy this folder with all its content into zip
     */
    private fun copyFolderIntoZipRecursive(parentFolder: File, zipOutputStream: ZipOutputStream) {
        if (parentFolder.exists()) {
            parentFolder.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    copyFolderIntoZipRecursive(file, zipOutputStream)
                } else {
                    zipOutputStream.putNextEntry(ZipEntry("${parentFolder.absolutePath}/${file.name}"))
                    zipOutputStream.write(file.readBytes())
                }
            }
        }
    }

    /**
     * restore all settings from a file
     */
    actual fun restoreSettingsFromFile() {
        Application.nativeInstance.currentActivity?.openDocument(arrayOf("application/zip")) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->
                    Application.nativeInstance.contentResolver.openInputStream(uri)?.also { inputStream ->
                        //read input data
                        val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))

                        var entry = zipInputStream.nextEntry

                        while (entry != null) {
                            if (entry.isDirectory) {
                                //when it's a directory create new directory
                                File(Application.nativeInstance.filesDir.parent, entry.name).mkdirs()
                            } else {
                                //when it's a file copy file
                                val file = File(Application.nativeInstance.filesDir.parent, entry.name)
                                file.parent?.also { parentFile -> File(parentFile).mkdirs() }
                                file.createNewFile()
                                file.outputStream().apply {
                                    zipInputStream.copyTo(this)
                                    flush()
                                    close()
                                }
                            }
                            //go to next entry
                            entry = zipInputStream.nextEntry
                        }

                        inputStream.close()

                        Application.nativeInstance.restart()
                    }
                }
            }
        }
    }

    /**
     * share settings file but without sensitive data
     */
    actual fun shareSettingsFile() {
        val toRemove = arrayOf(
            SettingsEnum.HttpClientServerEndpointHost.name,
            SettingsEnum.HttpClientServerEndpointPort.name,
            SettingsEnum.HttpServerPort.name,
            SettingsEnum.HttpServerSSLKeyStoreFile.name,
            SettingsEnum.HttpServerSSLKeyStorePassword.name,
            SettingsEnum.HttpServerSSLKeyAlias.name,
            SettingsEnum.HttpServerSSLKeyPassword.name,
            SettingsEnum.MQTTHost.name,
            SettingsEnum.MQTTPort.name,
            SettingsEnum.MQTTUserName.name,
            SettingsEnum.MQTTSSLEnabled.name,
            SettingsEnum.MQTTPassword.name,
            SettingsEnum.MQTTKeyStoreFile.name,
            SettingsEnum.WakeWordUDPOutputHost.name,
            SettingsEnum.WakeWordUDPOutputPort.name,
            SettingsEnum.WakeWordPorcupineAccessToken.name,
            SettingsEnum.SpeechToTextHttpEndpoint.name,
            SettingsEnum.IntentRecognitionHttpEndpoint.name,
            SettingsEnum.TextToSpeechHttpEndpoint.name,
            SettingsEnum.AudioPlayingHttpEndpoint.name,
            SettingsEnum.IntentHandlingEndpoint.name,
            SettingsEnum.IntentHandlingHassUrl.name,
            SettingsEnum.IntentHandlingHassAccessToken.name
        )

        //copy org.rhasspy.mobile.android_prefenrences.xml
        val sharedPreferencesFile = File(Application.nativeInstance.filesDir.parent, "shared_prefs/org.rhasspy.mobile.android_preferences.xml")
        val exportFile = File(Application.nativeInstance.filesDir, "org.rhasspy.mobile.android_preferences_export.xml")
        //create new empty file
        if (!exportFile.exists()) {
            exportFile.createNewFile()
        }
        exportFile.writeText("")
        //write data
        if (sharedPreferencesFile.exists()) {
            sharedPreferencesFile.readLines().forEach { line ->
                val name = line.substringAfter("\"").substringBefore("\"")
                val text = if (toRemove.contains(name)) {
                    if (line.contains("int")) { //value="1"
                        //replace value
                        line.replace(Regex("value=\".*\""), "value=\"***\"")
                    } else if (line.contains("string")) {
                        //replace between ><
                        line.replace(Regex(">.*</string>"), ">***</string>")
                    } else {
                        ""
                    }
                } else {
                    "$line\n"
                }
                exportFile.appendText(text)
            }
        }
        //share file
        val fileUri: Uri = FileProvider.getUriForFile(
            Application.nativeInstance,
            Application.nativeInstance.packageName.toString() + ".provider",
            exportFile
        )
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = "application/xml"
        }
        Application.nativeInstance.startActivity(Intent.createChooser(shareIntent, null).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        })
    }

}