package org.rhasspy.mobile.ui.content.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
actual fun DialogContainer(content: @Composable () -> Unit) {

    Popup(
        content = content,
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            excludeFromSystemGesture = true,
            clippingEnabled = true,
        )
    )

}