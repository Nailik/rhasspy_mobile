package org.rhasspy.mobile.viewmodel.utils

import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.platformspecific.clipboard.ClipboardUtils
import org.rhasspy.mobile.platformspecific.external.ExternalRedirect
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectIntention
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult
import org.rhasspy.mobile.resources.MR

object OpenLinkUtils {

    /**
     * true: link was opened
     * false: link could not be opened, url copied to clipboard
     */
    fun openLink(link: LinkType): Boolean {
        return if (ExternalRedirect.launch(ExternalRedirectIntention.OpenLink(link)) is ExternalRedirectResult.Success) {
            true
        } else {
            ClipboardUtils.copyToClipboard(MR.strings.url, link.url)
            false
        }
    }

}