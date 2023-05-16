package org.rhasspy.mobile.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.ui.permissions.RequiresMicrophonePermission
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabUiEvent.Action.UserSessionClick
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.TogglePlayRecording
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewState

/**
 * Home Screen contains
 *
 * Title bar (app name)
 * Service status information
 * wake up button
 * play recording button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val viewModel: HomeScreenViewModel = LocalViewModelFactory.current.getViewModel()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(MR.strings.appName.stable) }
            )
        },
    ) { paddingValues ->

        val viewState by viewModel.viewState.collectAsState()

        PortraitContent(
            paddingValues = paddingValues,
            viewState = viewState,
            onEvent = viewModel::onEvent
        )
        /*
        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                PortraitContent(
                    paddingValues = paddingValues,
                    viewState = viewState,
                    onEvent = viewModel::onEvent
                )
            }

            else -> {
                LandscapeContent(
                    paddingValues = paddingValues,
                    viewState = viewState,
                    onEvent = viewModel::onEvent
                )
            }
        }*/

    }

}

/**
 * Contains Column with
 *
 * Service status information
 * wake up button
 * play recording button
 */
@Composable
private fun PortraitContent(
    paddingValues: PaddingValues,
    viewState: HomeScreenViewState,
    onEvent: (event: HomeScreenUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            MicrophoneFabElement()
        }

        PlayRecording(
            isPlaying = viewState.isPlayingRecording,
            isPlayingRecordingEnabled = viewState.isPlayingRecordingEnabled,
            onEvent = onEvent
        )
    }
}

/**
 * Contains Row and Columns with
 *
 * Service status information
 * wake up button
 * play recording button
 */
@Composable
fun LandscapeContent(
    paddingValues: PaddingValues,
    viewState: HomeScreenViewState,
    onEvent: (event: HomeScreenUiEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            MicrophoneFabElement()
        }

        PlayRecording(
            modifier = Modifier.weight(1f),
            isPlaying = viewState.isPlayingRecording,
            isPlayingRecordingEnabled = viewState.isPlayingRecordingEnabled,
            onEvent = onEvent
        )
    }

}


@Composable
private fun MicrophoneFabElement() {
    val viewModel: MicrophoneFabViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()

    if (viewState.isMicrophonePermissionRequired) {
        RequiresMicrophonePermission(
            informationText = MR.strings.microphonePermissionInfoRecord.stable,
            onClick = { viewModel.onEvent(UserSessionClick) }
        ) { onClick ->
            MicrophoneFab(
                modifier = Modifier.fillMaxSize(),
                iconSize = 96.dp,
                viewState = viewState,
                onEvent = { onClick() }
            )
        }
    } else {
        MicrophoneFab(
            modifier = Modifier.fillMaxSize(),
            iconSize = 96.dp,
            viewState = viewState,
            onEvent = viewModel::onEvent
        )
    }
}


/**
 * button to play latest recording
 */
@Composable
private fun PlayRecording(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isPlayingRecordingEnabled: Boolean,
    onEvent: (event: HomeScreenUiEvent) -> Unit
) {
    FilledTonalButtonListItem(
        modifier = modifier,
        onClick = { onEvent(TogglePlayRecording) },
        enabled = isPlayingRecordingEnabled,
        icon = if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
        text = if (isPlaying) MR.strings.stopPlayRecording.stable else MR.strings.playRecording.stable
    )

}