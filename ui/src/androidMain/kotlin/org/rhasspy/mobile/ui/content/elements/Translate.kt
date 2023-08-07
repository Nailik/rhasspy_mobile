package org.rhasspy.mobile.ui.content.elements

import android.content.Context
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.StableStringResource.StableResourceFormattedStringDesc
import org.rhasspy.mobile.data.resource.StableStringResource.StableStringResourceSingle
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual object Translate : KoinComponent {

    actual fun translate(resource: StableStringResource): String {
        return when (resource) {
            is StableResourceFormattedStringDesc -> resource.stringResource.toString(get<NativeApplication>() as Context)
            is StableStringResourceSingle        -> StringDesc.Resource(resource.stringResource).toString(get<NativeApplication>() as Context)
        }
    }

    actual fun translate(resource: StableStringResourceSingle, arg: String): String {
        return StringDesc.ResourceFormatted(resource.stringResource, arg).toString(get<NativeApplication>() as Context)
    }

}