package org.rhasspy.mobile.android.content.list

import androidx.compose.foundation.clickable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.android.content.elements.Text

@Composable
fun RadioButtonListItem(
    modifier: Modifier = Modifier,
    text: StringResource,
    isChecked: Boolean,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    ListElement(
        modifier = modifier.clickable(onClick = onClick),
        icon = { RadioButton(selected = isChecked, onClick = onClick) },
        text = { Text(text) },
        trailing = trailing
    )
}

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
        icon = { RadioButton(selected = isChecked, onClick = onClick) },
        text = { Text(text) },
        trailing = trailing
    )
}