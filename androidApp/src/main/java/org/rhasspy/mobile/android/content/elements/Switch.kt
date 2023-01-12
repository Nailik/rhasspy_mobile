package org.rhasspy.mobile.android.content.elements

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
) {
    Switch(
        modifier = Modifier
            .clearAndSetSemantics {
                testTag = "SWITCH_TAG"
                role = Role.Switch
                toggleableState = if (checked) {
                    ToggleableState.On
                } else {
                    ToggleableState.Off
                }
            },
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}


