package org.rhasspy.mobile.ui.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

@Composable
fun SecondaryContent(
    visible: Boolean,
    content: (@Composable () -> Unit),
) {
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = visible
    ) {
        CompositionLocalProvider(
            LocalAbsoluteTonalElevation provides 0.dp
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                content = content
            )
        }
    }
}