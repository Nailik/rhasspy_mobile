package org.rhasspy.mobile.ui.content.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup

@Composable
actual fun DialogContainer(content: @Composable () -> Unit) {

    Popup(
        content = content
    )

}