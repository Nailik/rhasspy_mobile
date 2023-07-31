package androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings.sound

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
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.sounds.SoundOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.*
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.RadioButtonListItem
import org.rhasspy.mobile.ui.content.list.SliderListItem
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.IndicationSettingsScreenDestination
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.settings.indication.sound.IIndicationSoundSettingsViewModel

/**
 * screen to choose indication sound
 */
@Composable
fun IndicationSoundScreen(
    viewModel: IIndicationSoundSettingsViewModel,
    screen: IndicationSettingsScreenDestination,
    title: StableStringResource
) {
    Screen(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        val snackBarHostState = LocalSnackBarHostState.current
        val snackBarText = viewState.snackBarText?.let { translate(it) }

        LaunchedEffect(snackBarText) {
            snackBarText?.also {
                snackBarHostState.showSnackbar(message = it)
                viewModel.onEvent(ShowSnackBar)
            }
        }

        Scaffold(
            modifier = Modifier
                .testTag(screen)
                .fillMaxSize(),
            topBar = {
                AppBar(
                    title = title,
                    onEvent = viewModel::onEvent
                )
            }
        ) { paddingValues ->

            Surface(Modifier.padding(paddingValues)) {
                Column(
                    modifier = Modifier
                        .testTag(TestTag.IndicationSoundScreen)
                        .fillMaxSize()
                ) {

                    SoundElements(
                        modifier = Modifier.weight(1f),
                        soundSetting = viewState.soundSetting,
                        customSoundFiles = viewState.customSoundFiles,
                        onEvent = viewModel::onEvent
                    )

                    Card(
                        modifier = Modifier.padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                            value = viewState.soundVolume,
                            onValueChange = { viewModel.onEvent(UpdateSoundVolume(it)) }
                        )

                        SoundActionButtons(
                            onPlay = { viewModel.onEvent(ToggleAudioPlayerActive) },
                            isPlaying = viewState.isAudioPlaying,
                            onChooseFile = { viewModel.onEvent(ChooseSoundFile) }
                        )
                    }
                }

            }

        }

    }
}

/**
 * list element for sound item
 */
@Composable
private fun SoundElements(
    modifier: Modifier,
    soundSetting: String,
    customSoundFiles: ImmutableList<String>,
    onEvent: (IIndicationSoundSettingsUiEvent) -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {

        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.Default),
            text = MR.strings.defaultText.stable,
            isChecked = soundSetting == SoundOption.Default.name,
            onClick = { onEvent(SetSoundIndicationOption(SoundOption.Default)) }
        )

        RadioButtonListItem(
            modifier = Modifier.testTag(TestTag.Disabled),
            text = MR.strings.disabled.stable,
            isChecked = soundSetting == SoundOption.Disabled.name,
            onClick = { onEvent(SetSoundIndicationOption(SoundOption.Disabled)) }
        )

        //added files
        customSoundFiles.forEach { item ->
            SoundListItem(
                isSelected = item == soundSetting,
                soundFile = item,
                onClick = { onEvent(SetSoundFile(item)) },
                onDelete = { onEvent(DeleteSoundFile(item)) }
            )
        }
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

/**
 * app bar for indication screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    title: StableStringResource,
    onEvent: (event: IIndicationSoundSettingsUiEvent) -> Unit
) {

    TopAppBar(
        title = {
            Text(title)
        },
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

}