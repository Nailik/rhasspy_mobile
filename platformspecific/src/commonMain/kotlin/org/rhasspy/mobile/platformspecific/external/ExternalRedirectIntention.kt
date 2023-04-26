package org.rhasspy.mobile.platformspecific.external

import org.rhasspy.mobile.data.link.LinkType

sealed interface ExternalRedirectIntention<R> {

    /**
     * Create a Document return the uri as string
     */
    data class CreateDocument(
        val title: String,
        val mimeType: String
    ) : ExternalRedirectIntention<String>

    /**
     * Open Document and read content, opens at initial folder
     */
    data class OpenDocument(
        val uri: String,
        val mimeTypes: List<String>
    ) : ExternalRedirectIntention<String>

    /**
     * Get Document content, opens at initial folder
     * Alternative to OpenDocument
     */
    data class GetContent(
        val uri: String,
        val mimeTypes: List<String>
    ) : ExternalRedirectIntention<String>

    object OpenBatteryOptimizationSettings : ExternalRedirectIntention<Unit>

    object RequestMicrophonePermissionExternally : ExternalRedirectIntention<Unit>

    object OpenOverlaySettings : ExternalRedirectIntention<Unit>

    object OpenAppSettings : ExternalRedirectIntention<Unit>

    data class OpenLink(
        val link: LinkType
    ) : ExternalRedirectIntention<Unit>

    data class ShareFile(
        val fileUri: String,
        val mimeType: String,
    ) : ExternalRedirectIntention<Unit>

}