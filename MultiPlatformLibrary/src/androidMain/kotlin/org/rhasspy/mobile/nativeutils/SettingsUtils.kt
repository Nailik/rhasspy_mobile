package org.rhasspy.mobile.nativeutils

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.core.content.FileProvider
import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.fileutils.FolderType
import org.rhasspy.mobile.settings.SettingsEnum
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


actual object SettingsUtils {

    private val logger = Logger.withTag("SettingsUtils")

    /**
     * export the settings file
     */
    actual fun exportSettingsFile() {
        logger.d { "exportSettingsFile" }
        //to load zip export file
        Application.nativeInstance.currentActivity?.createDocument(
            "rhasspy_settings_${Clock.System.now().toLocalDateTime(TimeZone.UTC)}.zip",
            "application/zip"
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->
                    logger.d { "exportSettingsFile $uri" }
                    Application.nativeInstance.contentResolver.openOutputStream(uri)
                        ?.also { outputStream ->

                            //create output for zip file
                            val zipOutputStream =
                                ZipOutputStream(BufferedOutputStream(outputStream))

                            //shared Prefs file
                            zipOutputStream.putNextEntry(ZipEntry("shared_prefs/"))

                            //copy org.rhasspy.mobile.android_prefenrences.xml
                            val sharedPreferencesFile = File(
                                Application.nativeInstance.filesDir.parent,
                                "shared_prefs/org.rhasspy.mobile.android_preferences.xml"
                            )
                            if (sharedPreferencesFile.exists()) {
                                zipOutputStream.putNextEntry(ZipEntry("shared_prefs/${sharedPreferencesFile.name}"))
                                zipOutputStream.write(sharedPreferencesFile.readBytes())
                            }

                            zipOutputStream.closeEntry()
                            zipOutputStream.putNextEntry(ZipEntry("files/"))

                            //all custom files
                            val files = Application.nativeInstance.filesDir
                            FolderType.values().forEach { folderType ->
                                File(files, folderType.toString()).walkTopDown().forEach { file ->
                                    if (file.exists()) {
                                        if (file.isDirectory) {
                                            zipOutputStream.putNextEntry(ZipEntry("files/${folderType}/"))
                                            zipOutputStream.closeEntry()
                                        } else {
                                            zipOutputStream.putNextEntry(ZipEntry("files/${folderType}/${file.name}"))
                                            zipOutputStream.write(file.readBytes())
                                            zipOutputStream.closeEntry()
                                        }
                                    }
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

    /**
     * restore all settings from a file
     */
    actual fun restoreSettingsFromFile() {
        logger.d { "restoreSettingsFromFile" }

        Application.nativeInstance.currentActivity?.openDocument(arrayOf("application/zip")) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.also { uri ->
                    logger.d { "restoreSettingsFromFile $uri" }
                    Application.nativeInstance.contentResolver.openInputStream(uri)
                        ?.also { inputStream ->
                            //read input data
                            val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))

                            var entry = zipInputStream.nextEntry
                            val dir = Application.nativeInstance.filesDir.parent ?: ""

                            while (entry != null) {
                                val file = File(dir, entry.name)
                                val canonicalPath: String = file.canonicalPath
                                //necessary to hide play store warning
                                try {
                                    if (!canonicalPath.startsWith(dir)) {
                                        // SecurityException
                                        throw SecurityException("Path Traversal Vulnerability")
                                    }
                                }catch (_: Exception) {}
                                // Finish unzipping…
                                if (entry.isDirectory) {
                                    //when it's a directory create new directory
                                    file.mkdirs()
                                } else {
                                    //when it's a file copy file
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
                            Application.instance.restart()
                        }
                }
            }
        }
    }

    /**
     * share settings file but without sensitive data
     */
    actual fun shareSettingsFile() {
        logger.d { "shareSettingsFile" }

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
        val sharedPreferencesFile = File(
            Application.nativeInstance.filesDir.parent,
            "shared_prefs/org.rhasspy.mobile.android_preferences.xml"
        )
        val exportFile = File(
            Application.nativeInstance.filesDir,
            "org.rhasspy.mobile.android_preferences_export.xml"
        )
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
                    line
                }
                exportFile.appendText("$text\r\n")
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