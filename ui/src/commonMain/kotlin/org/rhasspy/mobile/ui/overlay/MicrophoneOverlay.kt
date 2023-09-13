package org.rhasspy.mobile.ui.overlay

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.combinedTestTag
import org.rhasspy.mobile.ui.main.MicrophoneFab
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Action.ToggleUserSession
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayViewModel

@Composable
fun MicrophoneOverlay(
    viewModel: MicrophoneOverlayViewModel,
    onDrag: (offset: Offset) -> Unit
) {
    val viewState by viewModel.viewState.collectAsState()

    MicrophoneFab(
        modifier = androidx.compose.ui.Modifier
            .size(viewState.microphoneOverlaySize.dp)
            .combinedTestTag(TestTag.MicrophoneFab, TestTag.Overlay)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    onDrag(dragAmount)
                    change.consume()
                }
            },
        iconSize = (viewState.microphoneOverlaySize * 0.4).dp,
        viewState = viewState.microphoneFabViewState,
        onEvent = { viewModel.onEvent(ToggleUserSession) }
    )
}