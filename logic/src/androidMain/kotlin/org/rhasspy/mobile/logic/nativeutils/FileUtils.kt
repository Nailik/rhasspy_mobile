package org.rhasspy.mobile.logic.nativeutils

import androidx.core.net.toFile
import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.fileutils.FolderType
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * to open file selection or delete file from local app storage
 */
actual object FileUtils : KoinComponent {

    private val context = get<NativeApplication>()

    /**
     * open file selection and copy file to specific folder and return selected file name
     */
    actual suspend fun selectFile(folderType: FolderType): Path? =
        suspendCoroutine { continuation ->
            context.currentActivity?.openDocument(folderType.fileTypes) {
                continuation.resume(it.data?.data?.toFile()?.toOkioPath())
            }
        }

}