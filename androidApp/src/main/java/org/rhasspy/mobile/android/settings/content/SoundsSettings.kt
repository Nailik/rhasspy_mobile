package org.rhasspy.mobile.android.settings.content

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.settings.SoundsSettingsViewModel

/**
 * sound settings value
 * volume
 * custom file selection
 * wake, recorded, error sound
 */
@Preview
@Composable
fun SoundsSettingsContent(viewModel: SoundsSettingsViewModel = viewModel()) {

    PageContent(MR.strings.sounds) {

        Column {

            //sound volume
            SliderListItem(
                text = MR.strings.volume,
                value = viewModel.soundVolume.collectAsState().value,
                onValueChange = viewModel::updateSoundVolume
            )

            val allSounds = viewModel.customSounds.collectAsState().value.toTypedArray().addSoundItems()

            //wake sound
            DropDownStringList(
                overlineText = {
                    Text(MR.strings.wakeSound)
                },
                selected = viewModel.wakeSound.collectAsState().value,
                values = allSounds,
                onSelect = viewModel::selectWakeSoundFile
            )

            //recorded sound
            DropDownStringList(
                overlineText = {
                    Text(MR.strings.recordedSound)
                },
                selected = viewModel.recordedSound.collectAsState().value,
                values = allSounds,
                onSelect = viewModel::selectRecordedSoundFile
            )

            //error sound
            DropDownStringList(
                overlineText = {
                    Text(MR.strings.errorSound)
                },
                selected = viewModel.errorSound.collectAsState().value,
                values = allSounds,
                onSelect = viewModel::selectErrorSoundFile
            )

            CustomSoundFile(viewModel)

        }

    }

}

@Composable
private fun CustomSoundFile(viewModel: SoundsSettingsViewModel) {

    //drop down to select a custom file
    DropDownListRemovableWithFileOpen(
        overlineText = {
            Text(MR.strings.sounds)
        },
        title = {
            Text(MR.strings.selectCustomSoundFile)
        },
        values = viewModel.customSoundValues.collectAsState().value,
        onAdd = viewModel::selectCustomSoundFile,
        onRemove = viewModel::removeCustomSoundFile
    )

}

/**
 * adds default and select file to drop down
 */
@Composable
private fun Array<String>.addSoundItems(): Array<String> {
    return this.toMutableList().apply {
        this.addAll(0, listOf(translate(resource = MR.strings.defaultText), translate(resource = MR.strings.disabled)))
    }.toTypedArray()
}
