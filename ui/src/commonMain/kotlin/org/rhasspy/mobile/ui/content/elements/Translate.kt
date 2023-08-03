package org.rhasspy.mobile.ui.content.elements

import androidx.compose.runtime.Composable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.StableStringResource.StableStringResourceSingle


@Composable
expect fun translate(resource: StableStringResource): String

@Composable
expect fun translate(resource: StableStringResourceSingle, arg: String): String