package org.rhasspy.mobile.ui.content.elements

import androidx.compose.runtime.Composable
import org.rhasspy.mobile.data.resource.StableStringResource


@Composable
expect fun translate(resource: StableStringResource): String

@Composable
expect fun translate(resource: StableStringResource, arg: String): String