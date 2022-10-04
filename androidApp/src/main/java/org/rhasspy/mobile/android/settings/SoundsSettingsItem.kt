package org.rhasspy.mobile.android.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.DropDownListRemovableWithFileOpen
import org.rhasspy.mobile.android.utils.DropDownStringList
import org.rhasspy.mobile.android.utils.ExpandableListItem
import org.rhasspy.mobile.android.utils.SliderListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

@Composable
fun SoundsItem(viewModel: SettingsScreenViewModel) {

    ExpandableListItem(
        text = MR.strings.sounds,
        secondaryText = MR.strings.soundsText
    ) {
        Column {
            SliderListItem(
                text = MR.strings.volume,
                value = viewModel.soundVolume.collectAsState().value,
                onValueChange = viewModel::updateSoundVolume
            )

            val allSounds = viewModel.customSounds.collectAsState().value.toTypedArray().addSoundItems()

            DropDownStringList(
                overlineText = { Text(MR.strings.wakeSound) },
                selected = viewModel.wakeSound.collectAsState().value,
                values = allSounds,
                onSelect = viewModel::selectWakeSoundFile
            )

            DropDownStringList(
                overlineText = { Text(MR.strings.recordedSound) },
                selected = viewModel.recordedSound.collectAsState().value,
                values = allSounds,
                onSelect = viewModel::selectRecordedSoundFile
            )

            DropDownStringList(
                overlineText = { Text(MR.strings.errorSound) },
                selected = viewModel.errorSound.collectAsState().value,
                values = allSounds,
                onSelect = viewModel::selectErrorSoundFile
            )

            CustomSoundFile(viewModel)
        }

    }
}

@Composable
private fun CustomSoundFile(viewModel: SettingsScreenViewModel) {
    DropDownListRemovableWithFileOpen(
        overlineText = { Text(MR.strings.sounds) },
        title = { Text(MR.strings.selectCustomSoundFile) },
        values = viewModel.customSoundValuesUi.collectAsState().value,
        onAdd = viewModel::selectCustomSoundFile,
        onRemove = viewModel::removeCustomSoundFile
    )
}

@Composable
private fun Array<String>.addSoundItems(): Array<String> {
    return this.toMutableList().apply {
        this.addAll(0, listOf(translate(resource = MR.strings.defaultText), translate(resource = MR.strings.disabled)))
    }.toTypedArray()
}
