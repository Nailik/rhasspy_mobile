package org.rhasspy.mobile.android.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.FilledTonalButton
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.item.EventStateCard
import org.rhasspy.mobile.android.content.item.EventStateIcon
import org.rhasspy.mobile.android.navigation.BottomBarScreens
import org.rhasspy.mobile.android.navigation.NavigationParams
import org.rhasspy.mobile.middleware.EventState
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

/**
 * Home Screen contains
 *
 * Title bar (app name)
 * Service status information
 * wake up button
 * play recording button
 */
@Preview
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = koinViewModel()) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(MR.strings.appName) }
            )
        },
    ) { paddingValues ->

        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                PortraitContent(paddingValues, viewModel)
            }
            else -> {
                LandscapeContent(paddingValues, viewModel)
            }
        }

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
    viewModel: HomeScreenViewModel
) {

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        ServiceStatusInformation(viewModel)

        MicrophoneFab(
            modifier = Modifier
                .weight(1f),
            iconSize = 96.dp,
            viewModel = viewModel
        )

        PlayRecording(viewModel)

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
    viewModel: HomeScreenViewModel
) {

    Row(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        MicrophoneFab(
            modifier = Modifier
                .weight(1f),
            iconSize = 96.dp,
            viewModel = viewModel
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ServiceStatusInformation(viewModel)

            PlayRecording(viewModel)
        }
    }

}


/**
 * Service status with text and icon, colored
 *
 * Links to Configuration page, with scrolling to service that contains issue
 */
@Composable
private fun ServiceStatusInformation(viewModel: HomeScreenViewModel) {

    val navigate = LocalNavController.current
    val serviceState by viewModel.serviceState.collectAsState()

    EventStateCard(
        eventState = serviceState,
        onClick = {
            if (viewModel.isActionEnabled.value) {
                navigate.navigate(BottomBarScreens.ConfigurationScreen.appendOptionalParameter(NavigationParams.ScrollToError, true))
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EventStateIcon(serviceState)
            ServiceStateText(serviceState)
        }
    }

}

/**
 * Text for service state
 */
@Composable
private fun ServiceStateText(serviceState: EventState) {

    Text(
        resource = when (serviceState) {
            is EventState.Pending -> MR.strings.serviceStatusPendingText
            is EventState.Loading -> MR.strings.serviceStatusLoadingText
            is EventState.Success -> MR.strings.serviceStatusRunningText
            is EventState.Warning -> MR.strings.serviceStatusWarningText
            is EventState.Error -> MR.strings.serviceStatusErrorText
            is EventState.Disabled -> MR.strings.disabled
        }
    )

}

/**
 * button to play latest recording
 */
@Composable
private fun PlayRecording(viewModel: HomeScreenViewModel) {

    val isPlaying by viewModel.isPlayingRecording.collectAsState()

    FilledTonalButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = viewModel::togglePlayRecording,
        enabled = viewModel.isPlayingRecordingEnabled.collectAsState().value,
        icon = if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
        text = if (isPlaying) MR.strings.stopPlayRecording else MR.strings.playRecording
    )

}