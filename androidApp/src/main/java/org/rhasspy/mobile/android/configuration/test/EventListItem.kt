package org.rhasspy.mobile.android.configuration.test

import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import org.rhasspy.mobile.android.theme.warn
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.middleware.Event
import org.rhasspy.mobile.middleware.EventState
import org.rhasspy.mobile.middleware.name

//loading, positive, negative state, text
@Composable
fun EventListItem(event: Event) {

    val state by event.eventState.collectAsState()

    val contentColor = when (state) {
        EventState.Pending -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        EventState.Loading -> LocalContentColor.current
        is EventState.Success -> MaterialTheme.colorScheme.primary
        is EventState.Warning -> MaterialTheme.colorScheme.warn
        is EventState.Error -> MaterialTheme.colorScheme.error
    }

    ListItem(
        leadingContent = { TestIcon(state) },
        colors = ListItemDefaults.colors(
            leadingIconColor = contentColor,
            headlineColor = contentColor
        ),
        overlineText = {
            Text(event.eventType.name)
        },
        supportingText = {
            Text(
                when (state) {
                    EventState.Pending -> "Pending"
                    EventState.Loading -> "Loading"
                    is EventState.Success -> state.information ?: "No Details"
                    is EventState.Warning -> state.information ?: "No Details"
                    is EventState.Error -> state.toString()
                }
            )
        },
        headlineText = {
            Text(event.eventType.title)
        })
}

@Composable
private fun TestIcon(state: EventState) {

    val icon = when (state) {
        EventState.Pending -> Icons.Outlined.Pending
        EventState.Loading -> Icons.Outlined.RotateRight
        is EventState.Success -> Icons.Outlined.Done
        is EventState.Warning -> Icons.Outlined.Warning
        is EventState.Error -> Icons.Outlined.ErrorOutline
    }

    val rotation = if (state == EventState.Loading) {
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