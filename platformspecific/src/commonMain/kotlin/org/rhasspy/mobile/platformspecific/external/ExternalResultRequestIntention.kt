package org.rhasspy.mobile.platformspecific.external

import org.rhasspy.mobile.data.link.LinkType

sealed interface ExternalResultRequestIntention<R> {

    /**
     * Create a Document return the uri as string
     */
    data class CreateDocument(
        val title: String,
        val mimeType: String
    ) : ExternalResultRequestIntention<String>

    /**
     * Open Document and read content, opens at initial folder
     */
    data class OpenDocument(
        val uri: String,
        val mimeTypes: List<String>
    ) : ExternalResultRequestIntention<String>

    /**
     * Get Document content, opens at initial folder
     * Alternative to OpenDocument
     */
    data class GetContent(
        val uri: String,
        val mimeTypes: List<String>
    ) : ExternalResultRequestIntention<String>

    data object OpenBatteryOptimizationSettings : ExternalResultRequestIntention<Unit>

    data object RequestMicrophonePermissionExternally : ExternalResultRequestIntention<Unit>

    data object OpenOverlaySettings : ExternalResultRequestIntention<Unit>

    data object ScanQRCode : ExternalResultRequestIntention<String>

    data object OpenAppSettings : ExternalResultRequestIntention<Unit>

    data class OpenLink(
        val link: LinkType
    ) : ExternalResultRequestIntention<Unit>

    data class ShareFile(
        val fileUri: String,
        val mimeType: String,
    ) : ExternalResultRequestIntention<Unit>

}