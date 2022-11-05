package org.rhasspy.mobile.android.settings.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.settings.WakeWordIndicationSettingsViewModel

/**
 * wake word indication settings
 */
@Preview
@Composable
fun WakeWordIndicationSettingsContent(viewModel: WakeWordIndicationSettingsViewModel = viewModel()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreens.WakeWordIndicationSettings),
        title = MR.strings.wakeWordIndication) {

        Card(
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {

            //turn on display
            SwitchListItem(
                text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay,
                isChecked = viewModel.isWakeWordDetectionTurnOnDisplayEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordDetectionTurnOnDisplay
            )

            //light indication
            SwitchListItem(
                text = MR.strings.wakeWordLightIndication,
                isChecked = viewModel.isWakeWordLightIndicationEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordLightIndicationEnabled
            )

            //sound indication
            SwitchListItem(
                text = MR.strings.wakeWordSoundIndication,
                isChecked = viewModel.isWakeWordSoundIndicationEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordSoundIndicationEnabled
            )

        }

        //visibility of sounds settings
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.isSoundSettingsVisible.collectAsState().value
        ) {

            Card(
                modifier = Modifier.padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {

                ListElement {
                    Text(
                        resource = MR.strings.sound,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

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

}


@Composable
private fun CustomSoundFile(viewModel: WakeWordIndicationSettingsViewModel) {

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
