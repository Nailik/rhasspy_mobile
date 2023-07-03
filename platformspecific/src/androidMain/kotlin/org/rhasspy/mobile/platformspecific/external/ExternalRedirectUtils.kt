package org.rhasspy.mobile.platformspecific.external

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.file.FolderType

object ExternalRedirectUtils : KoinComponent {

    suspend fun openDocument(folder: String, folderType: FolderType): String? = openDocument(folder, folderType.fileTypes)

    suspend fun openDocument(folder: String, mimeTypes: Array<String>): String? {
        var result = get<IExternalResultRequest>().launchForResult(
            ExternalResultRequestIntention.OpenDocument(folder, mimeTypes.toList())
        )

        if (result is ExternalRedirectResult.Result) {
            return result.data
        }

        result = get<IExternalResultRequest>().launch(
            ExternalResultRequestIntention.GetContent(folder, mimeTypes.toList())
        )

        if (result is ExternalRedirectResult.Result) {
            return result.data
        }

        return null
    }

}