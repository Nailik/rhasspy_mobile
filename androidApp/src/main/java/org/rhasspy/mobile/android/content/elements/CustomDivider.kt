package org.rhasspy.mobile.android.content.elements

import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = modifier)
}
