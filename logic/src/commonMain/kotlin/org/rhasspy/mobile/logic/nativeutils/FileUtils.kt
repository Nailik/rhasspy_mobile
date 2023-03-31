package org.rhasspy.mobile.logic.nativeutils

import okio.Path
import org.rhasspy.mobile.logic.fileutils.FolderType

/**
 * to open file selection or delete file from local app storage
 */
expect object FileUtils {

    /**
     * open file selection and copy file to specific folder and return selected file name
     */
     suspend fun selectFile(folderType: FolderType): Path?

}