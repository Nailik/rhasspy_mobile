package org.rhasspy.mobile.ui.configuration.pipeline.indicationsound

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.sounds.IndicationSoundOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.combinedTestTag
import org.rhasspy.mobile.ui.content.LocalSnackBarHostState
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.RadioButtonListItem
import org.rhasspy.mobile.ui.content.list.SliderListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.TonalElevationLevel2
import org.rhasspy.mobile.ui.theme.TonalElevationLevel3
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.AudioPlayerViewState
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent.Action.ChooseSoundFile
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent.Action.ToggleAudioPlayerActive
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent.Change.SetSoundIndicationOption
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent.Change.UpdateSoundVolume
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationViewModel
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.PipelineConfigurationLocalIndicationSoundDestination

/**
 * screen to choose indication sound
 */
@Composable
fun IndicationSoundScreen(
    viewModel: IndicationSoundConfigurationViewModel,
    screen: PipelineConfigurationLocalIndicationSoundDestination,
    title: StableStringResource
) {

    ScreenContent(
        modifier = Modifier.testTag(screen),
        title = title,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel2,
    ) {

        val viewState by viewModel.viewState.collectAsState()

        val snackBarHostState = LocalSnackBarHostState.current
        val snackBarText = viewState.snackBarText?.let { translate(it) }

        LaunchedEffect(snackBarText) {
            snackBarText?.also {
                snackBarHostState.showSnackbar(message = it)
                viewModel.onEvent(ShowSnackBar)
            }
        }

        Column(
            modifier = Modifier
                .testTag(TestTag.IndicationSoundScreen)
                .fillMaxSize()
        ) {

            SoundElements(
                modifier = Modifier.weight(1f),
                soundOption = viewState.editData.option,
                onEvent = viewModel::onEvent
            )

            val audioPlayerViewState by viewState.audioPlayerViewState.collectAsState()
            AudioPlayerContent(
                volume = viewState.editData.volume,
                viewState = audioPlayerViewState,
                onEvent = viewModel::onEvent
            )

        }

    }

}


@Composable
private fun AudioPlayerContent(
    volume: Float,
    viewState: AudioPlayerViewState,
    onEvent: (IndicationSoundConfigurationUiEvent) -> Unit
) {
    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = TonalElevationLevel3)
    ) {

        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewState.isNoSoundInformationBoxVisible
        ) {

            ListElement(
                modifier = Modifier.testTag(TestTag.Warning),
                icon = {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = MR.strings.info.stable
                    )
                },
                text = { Text(MR.strings.audioOutputSilentOrDisabled.stable) },
                secondaryText = { Text(viewState.audioOutputOption.text) }
            )

        }

        SliderListItem(
            text = MR.strings.volume.stable,
            value = volume,
            onValueChange = { onEvent(UpdateSoundVolume(it)) }
        )

        SoundActionButtons(
            onPlay = { onEvent(ToggleAudioPlayerActive) },
            isPlaying = viewState.isAudioPlaying,
            onChooseFile = { onEvent(ChooseSoundFile) }
        )
    }
}

/**
 * list element for sound item
 */
@Composable
private fun SoundElements(
    modifier: Modifier,
    soundOption: IndicationSoundOption,
    onEvent: (IndicationSoundConfigurationUiEvent) -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {

        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.Default),
            text = MR.strings.defaultText.stable,
            isChecked = soundOption == IndicationSoundOption.Default,
            onClick = { onEvent(SetSoundIndicationOption(IndicationSoundOption.Default)) }
        )

        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.Disabled),
            text = MR.strings.disabled.stable,
            isChecked = soundOption == IndicationSoundOption.Disabled,
            onClick = { onEvent(SetSoundIndicationOption(IndicationSoundOption.Disabled)) }
        )

        //TODO visibility for custom
    }
}

/**
 * list item for sound file
 */
@Composable
private fun SoundListItem(
    isSelected: Boolean,
    soundFile: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    RadioButtonListItem(
        modifier = Modifier.testTag(soundFile),
        text = soundFile,
        isChecked = isSelected,
        trailing = {
            if (!isSelected) {
                IconButton(
                    modifier = Modifier.combinedTestTag(soundFile, TestTag.Delete),
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = MR.strings.defaultText.stable
                    )
                }
            }
        },
        onClick = onClick
    )

}

/**
 * sound action buttons (play and open file)
 */
@Composable
private fun SoundActionButtons(
    onPlay: () -> Unit,
    isPlaying: Boolean,
    onChooseFile: () -> Unit
) {
    Surface(
        shape = RectangleShape, //ListItemDefaults.shape,
        color = MaterialTheme.colorScheme.surface, //ListItemDefaults.containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface, //ListItemDefaults.contentColor,
        tonalElevation = 0.0.dp, //ListItemDefaults.Elevation,
        shadowElevation = 0.0.dp, //ListItemDefaults.Elevation,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            FilledTonalButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag(TestTag.PlayPause),
                onClick = onPlay,
                content = {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) MR.strings.stop.stable else MR.strings.play.stable
                    )
                    Spacer(
                        modifier = Modifier.size(ButtonDefaults.IconSpacing)
                    )
                    Text(if (isPlaying) MR.strings.stop.stable else MR.strings.play.stable)
                })

            //visibility of mqtt settings
            FilledTonalButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag(TestTag.SelectFile),
                onClick = onChooseFile,
                content = {
                    Icon(
                        imageVector = Icons.Filled.FileOpen,
                        contentDescription = MR.strings.fileOpen.stable
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(MR.strings.fileOpen.stable)
                })

        }
    }
}