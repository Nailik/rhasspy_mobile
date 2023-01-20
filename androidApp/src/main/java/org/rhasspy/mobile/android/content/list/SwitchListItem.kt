package org.rhasspy.mobile.android.content.list

import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.android.content.elements.Text

@Composable
fun SwitchListItem(
    modifier: Modifier = Modifier,
    text: StringResource,
    secondaryText: StringResource? = null,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)
) {
    ListElement(
        modifier = modifier.toggleable(value = isChecked, onValueChange = onCheckedChange),
        text = { Text(text) },
        secondaryText = secondaryText?.let { { Text(secondaryText) } } ?: run { null },
        trailing = {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        })
}

