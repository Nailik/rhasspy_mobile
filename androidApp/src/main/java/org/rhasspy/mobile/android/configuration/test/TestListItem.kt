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
import org.rhasspy.mobile.services.state.ServiceState
import org.rhasspy.mobile.services.state.State

//loading, positive, negative state, text
@Composable
fun TestListItem(state: ServiceState) {
    val contentColor = when (state.state) {
        State.Pending -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        State.Loading -> LocalContentColor.current
        State.Error -> MaterialTheme.colorScheme.error
        State.Success -> MaterialTheme.colorScheme.primary
    }

    ListItem(
        leadingContent = { TestIcon(state.state) },
        colors = ListItemDefaults.colors(
            leadingIconColor = contentColor,
            headlineColor = contentColor
        ),
        supportingText = {
            Text(state.description?.toString() ?: "")
        },
        headlineText = {
            Text(state.stateType.toString())
        })
}

@Composable
private fun TestIcon(state: State) {

    val icon = when (state) {
        State.Pending -> Icons.Outlined.Pending
        State.Loading -> Icons.Outlined.RotateRight
        State.Error -> Icons.Outlined.Close
        State.Success -> Icons.Outlined.Done
    }

    val rotation = if (state == State.Loading) {
        val infiniteTransition = rememberInfiniteTransition()
        val animateRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 359f, animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Restart
            )
        )
        animateRotation
    } else {
        0f
    }

    Icon(icon, "", modifier = Modifier.rotate(rotation))
}