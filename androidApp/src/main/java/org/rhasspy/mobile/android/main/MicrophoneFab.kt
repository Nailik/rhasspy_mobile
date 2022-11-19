package org.rhasspy.mobile.android.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

/**
 * Floating Action Button with microphone
 */
@Composable
fun Fab(modifier: Modifier = Modifier, iconSize: Dp, viewModel: HomeScreenViewModel) {
/*
    RequiresMicrophonePermission(MR.strings.microphonePermissionInfoRecord, viewModel::toggleSession) { onClick ->

        FloatingActionButton(
            onClick = onClick,
            modifier = modifier.testTag("fab"),
            containerColor = when (viewModel.currentState.collectAsState().value) {
                State.RecordingIntent -> MaterialTheme.colorScheme.errorContainer
                State.AwaitingHotWord -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            },
        ) {

            Icon(
                imageVector = if (MicrophonePermission.granted.collectAsState().value) Icons.Filled.Mic else Icons.Filled.MicOff,
                contentDescription = MR.strings.wakeUp,
                tint = if (viewModel.currentState.collectAsState().value == State.RecordingIntent) MaterialTheme.colorScheme.onErrorContainer else LocalContentColor.current,
                modifier = Modifier.size(iconSize)
            )
        }

    }*/
}