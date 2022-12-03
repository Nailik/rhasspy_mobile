package org.rhasspy.mobile.android.main

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.permissions.RequiresMicrophonePermission
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

/**
 * Floating Action Button with microphone
 */
@Composable
fun Fab(modifier: Modifier = Modifier, iconSize: Dp, viewModel: HomeScreenViewModel) {

    RequiresMicrophonePermission(MR.strings.microphonePermissionInfoRecord, viewModel::toggleSession) { onClick ->

        FloatingActionButton(
            onClick = onClick,
            modifier = modifier.testTag(TestTag.MicrophoneFab),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
            /*when (viewModel.currentState.collectAsState().value) {
                State.RecordingIntent -> MaterialTheme.colorScheme.errorContainer
                State.AwaitingHotWord -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            },*/
        ) {

            Icon(
                imageVector = if (viewModel.isOkState.collectAsState().value) Icons.Filled.Mic else Icons.Filled.MicOff,
                contentDescription = MR.strings.wakeUp,
                tint = LocalContentColor.current,
                //if (viewModel.currentState.collectAsState().value == State.RecordingIntent) MaterialTheme.colorScheme.onErrorContainer else
                //  LocalContentColor.current,
                modifier = Modifier.size(iconSize)
            )
        }

    }
}