package org.rhasspy.mobile.ui.content.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.StableStringResource.StableResourceFormattedStringDesc
import org.rhasspy.mobile.data.resource.StableStringResource.StableStringResourceSingle
import org.rhasspy.mobile.settings.AppSetting

@Composable
actual fun translate(resource: StableStringResource): String {
    if (!LocalInspectionMode.current) {
        AppSetting.languageType.data.collectAsState().value
    }

    return when (resource) {
        is StableResourceFormattedStringDesc -> resource.stringResource.toString(LocalContext.current)
        is StableStringResourceSingle        -> StringDesc.Resource(resource.stringResource)
            .toString(LocalContext.current)
    }
}

@Composable
actual fun translate(resource: StableStringResourceSingle, arg: String): String {
    if (!LocalInspectionMode.current) {
        AppSetting.languageType.data.collectAsState().value
    }
    return StringDesc.ResourceFormatted(resource.stringResource, arg).toString(LocalContext.current)
}