package org.rhasspy.mobile.ui.content.list

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ListElement(
    modifier: Modifier = Modifier,
    colors: ListItemColors = ListItemDefaults.colors(),
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    ListItem(
        headlineContent = text,
        modifier = modifier,
        overlineContent = overlineText,
        supportingContent = secondaryText,
        leadingContent = icon,
        trailingContent = trailing,
        colors = colors,
    )
}



