package org.rhasspy.mobile.platformspecific.external

import org.rhasspy.mobile.platformspecific.file.FolderType

object ExternalRedirectUtils {

    suspend fun openDocument(folder: String, folderType: FolderType): String? = openDocument(folder, folderType.fileTypes)

    suspend fun openDocument(folder: String, mimeTypes: Array<String>): String? {
        var result = ExternalRedirect.launchForResult(
            ExternalRedirectIntention.OpenDocument(folder, mimeTypes.toList())
        )

        if (result is ExternalRedirectResult.Result) {
            return result.data
        }

        result = ExternalRedirect.launch(
            ExternalRedirectIntention.GetContent(folder, mimeTypes.toList())
        )

        if (result is ExternalRedirectResult.Result) {
            return result.data
        }

        return null
    }

}