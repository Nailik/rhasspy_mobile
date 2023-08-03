package org.rhasspy.mobile.platformspecific.file

import okio.Path

/**
 * to open file selection or delete file from local app storage
 */
actual object FileUtils {

    /**
     * open file selection and copy file to specific folder and return selected file name
     */
    actual suspend fun selectFile(folderType: FolderType): Path? {
        //TODO("Not yet implemented")
        return null
    }

}