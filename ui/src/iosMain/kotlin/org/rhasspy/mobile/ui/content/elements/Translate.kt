package org.rhasspy.mobile.ui.content.elements

import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.StableStringResource.StableResourceFormattedStringDesc
import org.rhasspy.mobile.data.resource.StableStringResource.StableStringResourceSingle


actual object Translate {

    actual fun translate(resource: StableStringResource): String {
        return when (resource) {
            is StableResourceFormattedStringDesc -> resource.stringResource.localized()
            is StableStringResourceSingle        -> StringDesc.Resource(resource.stringResource)
                .localized()
        }
    }

    actual fun translate(resource: StableStringResourceSingle, arg: String): String {
        return StringDesc.ResourceFormatted(resource.stringResource, arg).localized()
    }

}