package org.rhasspy.mobile.android.content.list

import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.ui.content.elements.Text

@Composable
fun CheckBoxListItem(
    modifier: Modifier = Modifier,
    text: StableStringResource,
    secondaryText: StableStringResource? = null,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)
) {
    ListElement(
        modifier = modifier.toggleable(
            value = isChecked,
            onValueChange = onCheckedChange
        ),
        icon = {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        },
        text = { Text(text) },
        secondaryText = if (secondaryText != null) {
            { Text(secondaryText) }
        } else null
    )
}