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
import org.rhasspy.mobile.ui.content.LocalViewModelFactory
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.ui.overlay.IndicationContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewState
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.HomeScreen
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.MicrophoneFabClick
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.TogglePlayRecording
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewModel

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

    ScreenContent(screenViewModel = viewModel) {
        Scaffold(
            modifier = Modifier
                .testTag(HomeScreen)
                .fillMaxSize(),
            topBar = {
                TopAppBar(title = { Text(MR.strings.appName.stable) })
            },
        ) { paddingValues ->

            val viewState by viewModel.viewState.collectAsState()

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                ) {
                    MicrophoneFabElement(
                        viewState = viewState.microphoneFabViewState,
                        onEvent = viewModel::onEvent
                    )

                    Column {
                        Spacer(modifier = Modifier.weight(1f))
                        IndicationContent()
                    }
                }

                PlayRecording(
                    isPlaying = viewState.isPlayingRecording,
                    isPlayingRecordingEnabled = viewState.isPlayingRecordingEnabled,
                    onEvent = viewModel::onEvent
                )

            }

        }
    }
}


@Composable
private fun MicrophoneFabElement(
    viewState: MicrophoneFabViewState,
    onEvent: (event: HomeScreenUiEvent) -> Unit
) {
    MicrophoneFab(
        modifier = Modifier.fillMaxSize(),
        iconSize = 96.dp,
        viewState = viewState,
        onEvent = { onEvent(MicrophoneFabClick) }
    )
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