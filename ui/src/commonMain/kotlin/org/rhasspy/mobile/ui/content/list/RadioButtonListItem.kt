package org.rhasspy.mobile.ui.content.list

import androidx.compose.foundation.clickable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.ui.content.elements.Text

@Composable
fun RadioButtonListItem(
    modifier: Modifier = Modifier,
    text: StableStringResource,
    isChecked: Boolean,
    enabled: Boolean = true,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    ListElement(
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        icon = {
            RadioButton(
                selected = isChecked,
                enabled = enabled,
                onClick = onClick
            )
        },
        text = { Text(text) },
        trailing = trailing
    )
}

@Composable
fun RadioButtonListItem(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    enabled: Boolean = true,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    ListElement(
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        icon = {
            RadioButton(
                selected = isChecked,
                enabled = enabled,
                onClick = onClick
            )
        },
        text = { Text(text) },
        trailing = trailing
    )
}