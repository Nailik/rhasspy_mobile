package org.rhasspy.mobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.rhasspy.mobile.data.event.Event
import org.rhasspy.mobile.data.event.EventState
import kotlin.coroutines.CoroutineContext

/**
 *  A side effect that gets executed when the given [event] changes to its triggered state.
 *
 *  @param event Pass the state event to be listened to from your view-state.
 *  @param onConsumed In this callback you are advised to set the passed [event] to [EventState.Consumed] in your view-state.
 *  @param action Callback that gets called in the composition's [CoroutineContext]. Perform the actual action this [event] leads to.
 */
@Composable
fun <T : Event> UiEventEffect(event: T, onConsumed: (event: T) -> Unit, action: (event: T) -> Unit) {
    LaunchedEffect(key1 = event, key2 = onConsumed) {
        if (event.eventState == EventState.Triggered) {
            action(event)
            onConsumed(event)
        }
    }
}