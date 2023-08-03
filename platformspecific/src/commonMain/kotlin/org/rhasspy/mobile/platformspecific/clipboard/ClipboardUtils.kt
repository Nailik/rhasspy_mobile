package org.rhasspy.mobile.platformspecific.clipboard

import dev.icerock.moko.resources.StringResource

expect object ClipboardUtils {

    fun copyToClipboard(label: StringResource, text: String)

}