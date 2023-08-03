package org.rhasspy.mobile.platformspecific.utils

import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.platformspecific.clipboard.ClipboardUtils
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult.Success
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention.OpenLink
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import org.rhasspy.mobile.resources.MR

interface IOpenLinkUtils {

    fun openLink(link: LinkType): Boolean

}

internal class OpenLinkUtils(
    private val externalResultRequest: IExternalResultRequest
) : IOpenLinkUtils {

    /**
     * true: link was opened
     * false: link could not be opened, url copied to clipboard
     */
    override fun openLink(link: LinkType): Boolean {
        return if (externalResultRequest.launch(OpenLink(link)) is Success) {
            true
        } else {
            ClipboardUtils.copyToClipboard(MR.strings.url, link.url)
            false
        }
    }

}