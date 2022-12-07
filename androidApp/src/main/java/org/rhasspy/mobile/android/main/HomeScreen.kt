package org.rhasspy.mobile.android.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.navigation.BottomBarScreens
import org.rhasspy.mobile.android.navigation.NavigationParams
import org.rhasspy.mobile.android.utils.FilledTonalButton
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.viewModels.HomeScreenViewModel


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

        ServiceErrorInformation()

        Fab(
            modifier = Modifier
                .weight(1f),
            iconSize = 96.dp,
            viewModel = viewModel
        )

        PlayRecording(viewModel)

    }
}

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

        Fab(
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
            ServiceErrorInformation()

            PlayRecording(viewModel)
        }
    }
}


/**
 * shows up when there are errors
 */
@Composable
private fun ServiceErrorInformation() {

    val navigate = LocalNavController.current

    Card(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        onClick = {
            navigate.navigate(BottomBarScreens.ConfigurationScreen.appendOptionalParameter(NavigationParams.ScrollToError, true))
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = MR.strings.error,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                resource = MR.strings.serviceErrorText,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }

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