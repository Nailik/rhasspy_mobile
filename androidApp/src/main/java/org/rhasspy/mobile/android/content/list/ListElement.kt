package org.rhasspy.mobile.android.content.list

import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ListElement(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    ListItem(
        headlineText = text,
        modifier = modifier,
        overlineText = overlineText,
        supportingText = secondaryText,
        leadingContent = icon,
        trailingContent = trailing
    )
}



