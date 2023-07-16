package org.rhasspy.mobile.ui.content.list

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.ui.content.elements.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioButtonListItem(
    modifier: Modifier = Modifier,
    text: StableStringResource,
    isChecked: Boolean,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    ListElement(
        modifier = modifier.clickable(onClick = onClick),
        icon = {
            RadioButton(
                selected = isChecked,
                onClick = onClick
            )
        },
        text = { Text(text) },
        trailing = trailing
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioButtonListItem(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    ListElement(
        modifier = modifier.clickable(onClick = onClick),
        icon = {
            RadioButton(
                selected = isChecked,
                onClick = onClick
            )
        },
        text = { Text(text) },
        trailing = trailing
    )
}