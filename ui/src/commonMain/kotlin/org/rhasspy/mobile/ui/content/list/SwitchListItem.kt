package org.rhasspy.mobile.ui.content.list

import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.ui.content.elements.Text

@Composable
fun SwitchListItem(
    modifier: Modifier = Modifier,
    text: StableStringResource,
    secondaryText: StableStringResource? = null,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
) {
    ListElement(
        modifier = modifier.toggleable(
            value = isChecked,
            onValueChange = onCheckedChange
        ),
        text = { Text(text) },
        secondaryText = secondaryText?.let { { Text(secondaryText) } },
        trailing = {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

