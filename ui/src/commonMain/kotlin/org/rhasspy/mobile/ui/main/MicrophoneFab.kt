package org.rhasspy.mobile.ui.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.FloatingActionButton
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabUiEvent
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabUiEvent.Action.MicrophoneFabClick
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewState

/**
 * Floating Action Button with microphone
 */
@Composable
fun MicrophoneFab(
    modifier: Modifier = Modifier,
    iconSize: Dp,
    viewState: MicrophoneFabViewState,
    onEvent: (MicrophoneFabUiEvent) -> Unit,
) {

    FloatingActionButton(
        modifier = modifier
            .testTag(TestTag.MicrophoneFab)
            .let {
                if (viewState.isShowBorder) {
                    return@let it.border(
                        8.dp,
                        MaterialTheme.colorScheme.errorContainer,
                        FloatingActionButtonDefaults.shape
                    )
                }
                it
            },
        onClick = {
            if (viewState.isUserActionEnabled) {
                onEvent(MicrophoneFabClick)
            }
        },
        isEnabled = viewState.isUserActionEnabled,
        containerColor = getContainerColorForMicrophoneFab(
            viewState.isUserActionEnabled,
            viewState.isRecording
        ),
        contentColor = getContentColorForMicrophoneFab(
            viewState.isUserActionEnabled,
            viewState.isRecording
        ),
        icon = {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = if (viewState.isMicrophonePermissionAllowed) Icons.Filled.Mic else Icons.Filled.MicOff,
                contentDescription = MR.strings.wakeUp.stable,
            )
        }
    )

}

@Composable
fun getContainerColorForMicrophoneFab(isActionEnabled: Boolean, isRecording: Boolean): Color {
    return when {
        isRecording -> MaterialTheme.colorScheme.errorContainer
        isActionEnabled -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    }
}

@Composable
fun getContentColorForMicrophoneFab(isActionEnabled: Boolean, isRecording: Boolean): Color {
    return when {
        isRecording -> MaterialTheme.colorScheme.onErrorContainer
        isActionEnabled -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f)
    }
}