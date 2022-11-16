package org.rhasspy.mobile.android.configuration.test

import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.RotateRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import org.rhasspy.mobile.viewModels.configuration.test.TestState

//loading, positive, negative state, text
@Composable
fun TestListItem(testState: TestState, text: String) {
    val contentColor = when (testState) {
        TestState.Pending -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        TestState.Loading -> LocalContentColor.current
        TestState.Negative -> MaterialTheme.colorScheme.error
        TestState.Positive -> MaterialTheme.colorScheme.primary
    }

    ListItem(
        leadingContent = { TestIcon(testState) },
        colors = ListItemDefaults.colors(
            leadingIconColor = contentColor,
            headlineColor = contentColor
        ),
        headlineText = {
            Text(text)
        })
}

@Composable
private fun TestIcon(testState: TestState){

    val icon = when(testState){
        TestState.Pending -> Icons.Outlined.Pending
        TestState.Loading -> Icons.Outlined.RotateRight
        TestState.Negative -> Icons.Outlined.Close
        TestState.Positive -> Icons.Outlined.Done
    }

    val rotation = if(testState == TestState.Loading) {
        val infiniteTransition = rememberInfiniteTransition()
        val animateRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 359f, animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Restart
            )
        )
        animateRotation
    }  else {
        0f
    }

        Icon(icon, "", modifier = Modifier.rotate(rotation))
}