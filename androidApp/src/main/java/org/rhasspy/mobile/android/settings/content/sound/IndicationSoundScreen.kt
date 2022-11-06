package org.rhasspy.mobile.android.settings.content.sound

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.combinedTestTag
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.RadioButtonListItem
import org.rhasspy.mobile.android.utils.SliderListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.settings.sounds.SoundFile
import org.rhasspy.mobile.viewModels.settings.sound.IIndicationSoundSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicationSoundScreen(viewModel: IIndicationSoundSettingsViewModel, title: StringResource, screen: IndicationSettingsScreens) {
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

                RadioButtonListItem(
                    text = MR.strings.defaultText,
                    isChecked = viewModel.isSoundIndicationDefault.collectAsState().value,
                    onClick = viewModel::onClickSoundIndicationDefault
                )

                RadioButtonListItem(
                    text = MR.strings.disabled,
                    isChecked = viewModel.isSoundIndicationDisabled.collectAsState().value,
                    onClick = viewModel::onClickSoundIndicationDisabled
                )

                val elements by viewModel.customSoundFiles.collectAsState()

                //added files
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
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

                SliderListItem(
                    text = MR.strings.volume,
                    value = viewModel.soundVolume.collectAsState().value,
                    onValueChange = viewModel::updateSoundVolume
                )

                SoundActionButtons(
                    onPlay = viewModel::playSoundFile,
                    onChooseFile = viewModel::chooseSoundFile
                )
            }
        }
    }
}

/**
 * list item for sound file
 */
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
/**
 * sound action buttons (play and open file)
 */
@Composable
private fun SoundActionButtons(
    onPlay: () -> Unit,
    onChooseFile: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        FilledTonalButton(
            modifier = Modifier.testTag(TestTag.Play),
            onClick = onPlay,
            content = {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = MR.strings.play
                )
                Spacer(
                    modifier = Modifier.size(ButtonDefaults.IconSpacing)
                )
                Text(MR.strings.play)
            })

        FilledTonalButton(
            modifier = Modifier.testTag(TestTag.SelectFile),
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
/**
 * app bar for indication screen
 */
@OptIn(ExperimentalMaterial3Api::class)
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
