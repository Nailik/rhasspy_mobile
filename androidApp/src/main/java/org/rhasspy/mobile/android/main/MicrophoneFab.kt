package org.rhasspy.mobile.android.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.FloatingActionButton
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.permissions.RequiresMicrophonePermission
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

/**
 * Floating Action Button with microphone
 */
@Composable
fun MicrophoneFab(modifier: Modifier = Modifier, iconSize: Dp, viewModel: HomeScreenViewModel = getViewModel()) {

    Box(modifier = modifier) {

        RequiresMicrophonePermission(MR.strings.microphonePermissionInfoRecord, viewModel::toggleSession) { onClick ->

            val isActionEnabled by viewModel.isActionEnabled.collectAsState()
            val isRecording by viewModel.isActionEnabled.collectAsState()

            FloatingActionButton(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(TestTag.MicrophoneFab)
                    .let {
                        if (viewModel.isHotWordRecording.collectAsState().value) {
                            return@let it.border(2.dp, MaterialTheme.colorScheme.errorContainer, FloatingActionButtonDefaults.shape)
                        }
                        it
                    },
                onClick = {
                    if (isActionEnabled) {
                        onClick()
                    }
                },
                isEnabled = isActionEnabled,
                containerColor = when {
                    isActionEnabled -> MaterialTheme.colorScheme.primaryContainer
                    isRecording -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                },
                contentColor = when {
                    isActionEnabled -> MaterialTheme.colorScheme.onPrimaryContainer
                    isRecording -> MaterialTheme.colorScheme.onErrorContainer
                    else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f)
                },
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                icon = {
                    Icon(
                        modifier = Modifier.size(iconSize),
                        imageVector = if (viewModel.isMicrophonePermissionGranted.collectAsState().value) Icons.Filled.Mic else Icons.Filled.MicOff,
                        contentDescription = MR.strings.wakeUp,
                    )
                }
            )

        }
    }
}

