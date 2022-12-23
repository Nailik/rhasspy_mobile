package org.rhasspy.mobile.nativeutils

import org.rhasspy.mobile.settings.FileType

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object FileUtils {//TODO try catch file doesn't exist

    suspend fun selectFile(fileType: FileType, subfolder: String? = null): String?

    fun removeFile(fileType: FileType, subfolder: String? = null, fileName: String)

}