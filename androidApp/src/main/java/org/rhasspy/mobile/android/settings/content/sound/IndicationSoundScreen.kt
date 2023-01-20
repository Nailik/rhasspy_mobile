package org.rhasspy.mobile.android.settings.content.sound

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.combinedTestTag
import org.rhasspy.mobile.android.content.OnPauseEffect
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.RadioButtonListItem
import org.rhasspy.mobile.android.content.list.SliderListItem
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.settings.sounds.SoundFile
import org.rhasspy.mobile.viewmodel.settings.sound.IIndicationSoundSettingsViewModel

/**
 * screen to choose indication sound
 */
@Composable
fun IndicationSoundScreen(
    viewModel: IIndicationSoundSettingsViewModel,
    title: StringResource,
    screen: IndicationSettingsScreens
) {

    OnPauseEffect(viewModel::onPause)

    Scaffold(
        modifier = Modifier
            .testTag(screen)
            .fillMaxSize(),
        topBar = { AppBar(title) }
    ) { paddingValues ->

        Surface(Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .testTag(TestTag.IndicationSoundScreen)
                    .fillMaxSize()
            ) {

                SoundElements(viewModel)

                Card(
                    modifier = Modifier.padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {

                    AnimatedVisibility(
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                        visible = viewModel.isNoSoundInformationBoxVisible.collectAsState().value
                    ) {

                        ListElement(
                            modifier = Modifier.testTag(TestTag.Warning),
                            icon = {
                                Icon(
                                    Icons.Filled.Info,
                                    contentDescription = MR.strings.info
                                )
                            },
                            text = { Text(MR.strings.audioOutputSilentOrDisabled) },
                            secondaryText = { Text(viewModel.audioOutputOption.collectAsState().value.text) }
                        )

                    }

                    SliderListItem(
                        text = MR.strings.volume,
                        value = viewModel.soundVolume.collectAsState().value,
                        onValueChange = viewModel::updateSoundVolume
                    )

                    SoundActionButtons(
                        onPlay = viewModel::toggleAudioPlayer,
                        isPlaying = viewModel.isAudioPlaying.collectAsState().value,
                        onChooseFile = viewModel::chooseSoundFile
                    )
                }
            }

        }

    }

}

/**
 * list element for sound item
 */
@Composable
private fun ColumnScope.SoundElements(viewModel: IIndicationSoundSettingsViewModel) {

    RadioButtonListItem(
        modifier = Modifier.testTag(TestTag.Default),
        text = MR.strings.defaultText,
        isChecked = viewModel.isSoundIndicationDefault.collectAsState().value,
        onClick = viewModel::onClickSoundIndicationDefault
    )

    RadioButtonListItem(
        modifier = Modifier.testTag(TestTag.Disabled),
        text = MR.strings.disabled,
        isChecked = viewModel.isSoundIndicationDisabled.collectAsState().value,
        onClick = viewModel::onClickSoundIndicationDisabled
    )

    val elements by viewModel.customSoundFiles.collectAsState()

    //added files
    LazyColumn(
        modifier = Modifier.weight(1f)
    ) {

        items(
            count = elements.size,
            itemContent = { index ->
                val element = elements[index]

                //element
                SoundListItem(
                    soundFile = element,
                    onClick = { viewModel.selectSoundFile(element) },
                    onDelete = { viewModel.deleteSoundFile(element) }
                )
            }
        )
    }

}

/**
 * list item for sound file
 */
@Composable
private fun SoundListItem(
    soundFile: SoundFile,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    RadioButtonListItem(
        modifier = Modifier.testTag(soundFile.fileName),
        text = soundFile.fileName,
        isChecked = soundFile.selected,
        trailing = {
            if (soundFile.canBeDeleted) {
                IconButton(
                    modifier = Modifier.combinedTestTag(soundFile.fileName, TestTag.Delete),
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = MR.strings.defaultText
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
                        contentDescription = if (isPlaying) MR.strings.stop else MR.strings.play
                    )
                    Spacer(
                        modifier = Modifier.size(ButtonDefaults.IconSpacing)
                    )
                    Text(if (isPlaying) MR.strings.stop else MR.strings.play)
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
                        contentDescription = MR.strings.fileOpen
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(MR.strings.fileOpen)
                })

        }
    }
}

/**
 * app bar for indication screen
 */
@Composable
private fun AppBar(title: StringResource) {

    val navigation = LocalNavController.current

    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(
                onClick = navigation::popBackStack,
                modifier = Modifier.testTag(TestTag.AppBarBackButton)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back,
                )
            }
        }
    )

}