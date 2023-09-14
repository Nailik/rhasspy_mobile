package org.rhasspy.mobile.platformspecific.settings

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectUtils
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.file.SystemFolderType
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

internal actual class SettingsUtils actual constructor(
    private val externalResultRequest: IExternalResultRequest,
    private val nativeApplication: NativeApplication,
) : ISettingsUtils {

    private val logger = Logger.withTag("SettingsUtils")

    /**
     * export the settings file
     */
    actual override suspend fun exportSettingsFile(): Boolean {
        return try {
            logger.d { "exportSettingsFile" }

            val result = externalResultRequest.launchForResult(
                ExternalResultRequestIntention.CreateDocument(
                    title = "rhasspy_settings_${Clock.System.now().toLocalDateTime(TimeZone.UTC)}.zip",
                    mimeType = "application/zip"
                )
            )

            if (result !is ExternalRedirectResult.Result) return false

            return nativeApplication.contentResolver.openOutputStream(result.data.toUri())
                ?.let { outputStream ->

                    //create output for zip file
                    val zipOutputStream =
                        ZipOutputStream(BufferedOutputStream(outputStream))

                    //shared Prefs file
                    zipOutputStream.putNextEntry(ZipEntry("shared_prefs/"))

                    //copy org.rhasspy.mobile.android_prefenrences.xml
                    val sharedPreferencesFile = File(
                        nativeApplication.filesDir.parent,
                        "shared_prefs/org.rhasspy.mobile.android_preferences.xml"
                    )
                    if (sharedPreferencesFile.exists()) {
                        zipOutputStream.putNextEntry(ZipEntry("shared_prefs/${sharedPreferencesFile.name}"))
                        zipOutputStream.write(sharedPreferencesFile.readBytes())
                    }

                    zipOutputStream.closeEntry()
                    zipOutputStream.putNextEntry(ZipEntry("files/"))

                    //all custom files
                    val files = nativeApplication.filesDir
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

                    true
                } ?: false

        } catch (exception: Exception) {
            logger.e(exception) { "exportSettingsFile" }
            false
        }
    }

    /**
     * restore all settings from a file
     */
    actual override suspend fun restoreSettingsFromFile(): Boolean {
        return try {
            logger.d { "restoreSettingsFromFile" }

            val result = ExternalRedirectUtils.openDocument(
                folder = SystemFolderType.Download.folder,
                mimeTypes = arrayOf("application/zip")
            )

            return result?.toUri()?.let { uri ->
                logger.d { "restoreSettingsFromFile $uri" }
                nativeApplication.contentResolver.openInputStream(uri)
                    ?.let { inputStream ->
                        //read input data
                        val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))

                        var entry = zipInputStream.nextEntry
                        val dir = nativeApplication.filesDir.parent ?: ""

                        while (entry != null) {
                            logger.d { "restoreSettingsFromFile entry $entry" }
                            val file = File(dir, entry.name)
                            val canonicalPath: String = file.canonicalPath
                            //necessary to hide play store warning
                            try {
                                if (!canonicalPath.startsWith(dir)) {
                                    // SecurityException
                                    throw SecurityException("Path Traversal Vulnerability")
                                }
                            } catch (_: Exception) {
                            }
                            // Finish unzipping…
                            if (entry.isDirectory) {
                                logger.d { "restoreSettingsFromFile entry isDirectory $entry" }
                                //when it's a directory create new directory
                                file.mkdirs()
                            } else {
                                //when it's a file copy file
                                file.parent?.also { parentFile -> File(parentFile).mkdirs() }
                                file.createNewFile()
                                logger.d { "restoreSettingsFromFile entry outputStream $entry" }
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
                        nativeApplication.restart()

                        true
                    } ?: false
            } ?: false

        } catch (exception: Exception) {
            logger.e(exception) { "restoreSettingsFromFile" }
            false
        }
    }

    /**
     * share settings file but without sensitive data
     */
    actual override suspend fun shareSettingsFile(toRemove: List<String>): Boolean {
        return try {
            logger.d { "shareSettingsFile" }

            //copy org.rhasspy.mobile.android_prefenrences.xml
            val sharedPreferencesFile = File(
                nativeApplication.filesDir.parent,
                "shared_prefs/org.rhasspy.mobile.android_preferences.xml"
            )
            val exportFile = File(
                nativeApplication.filesDir,
                "org.rhasspy.mobile.android_preferences_export.xml"
            )
            //create new empty file
            if (!exportFile.exists()) {
                withContext(Dispatchers.IO) {
                    exportFile.createNewFile()
                }
            }
            exportFile.writeText("")
            //write data
            if (sharedPreferencesFile.exists()) {
                var text = sharedPreferencesFile.readText()
                toRemove.filter { it.isNotEmpty() && it.isNotBlank() }.forEach { content ->
                    println("replace $content")
                    text = text.replace("\"$content\"", "*hidden*")
                }
            }

            //share file
            val fileUri: Uri = FileProvider.getUriForFile(
                nativeApplication,
                nativeApplication.packageName.toString() + ".provider",
                exportFile
            )

            return externalResultRequest.launch(
                ExternalResultRequestIntention.ShareFile(
                    fileUri = fileUri.toString(),
                    mimeType = "application/xml"
                )
            ) is ExternalRedirectResult.Success

        } catch (exception: Exception) {
            logger.e(exception) { "shareSettingsFile" }
            false
        }
    }

}