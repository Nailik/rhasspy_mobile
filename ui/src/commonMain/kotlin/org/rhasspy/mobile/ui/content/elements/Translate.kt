package org.rhasspy.mobile.ui.content.elements

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.StableStringResource.StableStringResourceSingle

expect object Translate {

    fun translate(resource: StableStringResource): String

    fun translate(resource: StableStringResourceSingle, arg: String): String

}