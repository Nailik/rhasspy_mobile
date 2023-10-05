package org.rhasspy.mobile.ui.configuration.domains.mic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.domains.mic.MicDomainState
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.DomainStateHeaderItem
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.TonalElevationLevel1
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.MicDomainConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.MicDomainConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.MicDomainConfigurationUiEvent.Change.SetUsePauseOnMediaPlayback
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.MicDomainConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.MicDomainConfigurationViewState
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.AudioInputConfigurationScreen

@Composable
fun AudioInputConfigurationScreen(viewModel: MicDomainConfigurationViewModel) {

    ScreenContent(
        modifier = Modifier.testTag(AudioInputConfigurationScreen),
        title = MR.strings.audio_input.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel1,
    ) {

        Column {
            val viewState by viewModel.viewState.collectAsState()
            val micDomainState by viewState.micDomainStateFlow.collectAsState()

            DomainStateHeaderItem(
                modifier = Modifier.then(
                    if (micDomainState is MicDomainState.MicrophonePermissionMissing) {
                        Modifier.clickable {
                            viewModel.onEvent(RequestMicrophonePermission)
                        }
                    } else Modifier
                ),
                domainStateFlow = viewState.domainStateFlow
            )

            AudioInputConfigurationScreenContent(
                onEvent = viewModel::onEvent,
                viewState = viewState
            )

        }
    }

}

@Composable
private fun AudioInputConfigurationScreenContent(
    onEvent: (MicDomainConfigurationUiEvent) -> Unit,
    viewState: MicDomainConfigurationViewState
) {

    Column(
        modifier = Modifier
            .testTag(TestTag.List)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        //button to open audio recorder format
        ListElement(
            modifier = Modifier.clickable { onEvent(OpenInputFormatConfigurationScreen) },
            text = { Text(MR.strings.audioRecorderFormat.stable) },
            secondaryText = @Composable {
                val text = "${translate(viewState.editData.audioInputChannel.text)} | " +
                        "${translate(viewState.editData.audioInputEncoding.text)} | " +
                        translate(viewState.editData.audioInputSampleRate.text)
                Text(text = text)
            }
        )

        HorizontalDivider()

        //button to open audio output format
        ListElement(
            modifier = Modifier.clickable { onEvent(OpenOutputFormatConfigurationScreen) },
            text = { Text(MR.strings.audioOutputFormat.stable) },
            secondaryText = @Composable {
                val text = "${translate(viewState.editData.audioOutputChannel.text)} | " +
                        "${translate(viewState.editData.audioOutputEncoding.text)} | " +
                        translate(viewState.editData.audioOutputSampleRate.text)
                Text(text = text)
            }
        )

        Divider()

        //isPauseRecordingOnMediaPlayback
        SwitchListItem(
            modifier = Modifier,
            text = MR.strings.autoStopRecording.stable,
            secondaryText = MR.strings.autoStopRecordingInformation.stable,
            isChecked = viewState.editData.isPauseRecordingOnMediaPlayback,
            isEnabled = viewState.isPauseRecordingOnMediaPlaybackEnabled,
            onCheckedChange = { onEvent(SetUsePauseOnMediaPlayback(it)) }
        )

    }

}