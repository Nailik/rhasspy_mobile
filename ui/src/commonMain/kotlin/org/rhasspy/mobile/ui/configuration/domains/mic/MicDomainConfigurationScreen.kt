package org.rhasspy.mobile.ui.configuration.domains.mic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationViewState
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.AudioInputConfigurationScreen

@Composable
fun AudioInputConfigurationScreen(viewModel: AudioInputConfigurationViewModel) {

    ScreenContent(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        AudioInputConfigurationScreenContent(
            onEvent = viewModel::onEvent,
            viewState = viewState
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioInputConfigurationScreenContent(
    onEvent: (AudioInputConfigurationUiEvent) -> Unit,
    viewState: AudioInputConfigurationViewState
) {

    Surface(tonalElevation = 3.dp) {
        Scaffold(
            modifier = Modifier
                .testTag(AudioInputConfigurationScreen)
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(MR.strings.audio_input.stable) },
                    navigationIcon = {
                        IconButton(
                            onClick = { onEvent(BackClick) },
                            modifier = Modifier.testTag(TestTag.AppBarBackButton)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = MR.strings.back.stable,
                            )
                        }
                    }
                )
            },
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .testTag(TestTag.List)
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {


                //button to open audio recorder format
                ListElement(
                    modifier = Modifier.clickable { onEvent(OpenInputFormatConfigurationScreen) },
                    text = { Text(MR.strings.audioRecorderFormat.stable) },
                    secondaryText = @Composable {
                        val text = "${translate(viewState.data.audioInputChannel.text)} | " +
                                "${translate(viewState.data.audioInputEncoding.text)} | " +
                                translate(viewState.data.audioInputSampleRate.text)
                        Text(text = text)
                    }
                )

                HorizontalDivider()

                //button to open audio output format
                ListElement(
                    modifier = Modifier.clickable { onEvent(OpenOutputFormatConfigurationScreen) },
                    text = { Text(MR.strings.audioOutputFormat.stable) },
                    secondaryText = @Composable {
                        val text = "${translate(viewState.data.audioOutputChannel.text)} | " +
                                "${translate(viewState.data.audioOutputEncoding.text)} | " +
                                translate(viewState.data.audioOutputSampleRate.text)
                        Text(text = text)
                    }
                )

            }
        }
    }

}