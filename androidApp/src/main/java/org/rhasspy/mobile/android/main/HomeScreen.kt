package org.rhasspy.mobile.android.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.FilledTonalButtonListItem
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.ListElement
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
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            ServiceErrorInformation(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            Fab(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                iconSize = 96.dp,
                viewModel = viewModel
            )

            Spacer(modifier = Modifier.height(8.dp))

            PlayRecording(viewModel)

            Spacer(modifier = Modifier.height(8.dp))

        }
    }

}

@Composable
private fun ServiceErrorInformation(viewModel: HomeScreenViewModel) {

    ListElement {
        Card(
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = MR.strings.error,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "error on 5 services",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }

}

/**
 * button to play latest recording
 */
@Composable
private fun PlayRecording(viewModel: HomeScreenViewModel) {
    val isPlaying = false//viewModel.currentState.collectAsState().value == State.PlayingRecording

    FilledTonalButtonListItem(
        onClick = viewModel::togglePlayRecording,
        icon = if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
        text = if (isPlaying) MR.strings.stopPlayRecording else MR.strings.playRecording
    )

}