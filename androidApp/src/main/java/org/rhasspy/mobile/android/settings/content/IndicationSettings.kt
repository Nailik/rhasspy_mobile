package org.rhasspy.mobile.android.settings.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.SecondaryContent
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.android.settings.content.sound.IndicationSettingsScreens
import org.rhasspy.mobile.android.settings.content.sound.IndicationSoundScreen
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.settings.IndicationSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.sound.WakeIndicationSoundSettingsViewModel

/**
 * indication sounds
 */
@Preview
@Composable
fun WakeWordIndicationSettingsContent(viewModel: IndicationSettingsViewModel = get()) {

    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = IndicationSettingsScreens.Overview.name
        ) {

            composable(IndicationSettingsScreens.Overview.name) {
                IndicationSettingsOverview(viewModel)
            }

            composable(IndicationSettingsScreens.WakeIndicationSound.name) {
                IndicationSoundScreen(
                    viewModel = get<WakeIndicationSoundSettingsViewModel>(),
                    title = MR.strings.wakeSound,
                    screen = IndicationSettingsScreens.WakeIndicationSound
                )
            }

            composable(IndicationSettingsScreens.RecordedIndicationSound.name) {
                IndicationSoundScreen(
                    viewModel = get<RecordedIndicationSoundSettingsViewModel>(),
                    title = MR.strings.recordedSound,
                    screen = IndicationSettingsScreens.RecordedIndicationSound
                )
            }

            composable(IndicationSettingsScreens.ErrorIndicationSound.name) {
                IndicationSoundScreen(
                    viewModel = get<ErrorIndicationSoundSettingsViewModel>(),
                    title = MR.strings.errorSound,
                    screen = IndicationSettingsScreens.ErrorIndicationSound
                )
            }

        }
    }

}

/**
 * wake word indication settings
 */
@Composable
fun IndicationSettingsOverview(viewModel: IndicationSettingsViewModel) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreenType.IndicationSettings),
        title = MR.strings.indication
    ) {

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {

            //turn on display
            SwitchListItem(
                modifier = Modifier.testTag(TestTag.WakeWordDetectionTurnOnDisplay),
                text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay,
                isChecked = viewModel.isWakeWordDetectionTurnOnDisplayEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordDetectionTurnOnDisplay
            )

            //light indication
            SwitchListItem(
                modifier = Modifier.testTag(TestTag.WakeWordLightIndicationEnabled),
                text = MR.strings.wakeWordLightIndication,
                isChecked = viewModel.isWakeWordLightIndicationEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordLightIndicationEnabled
            )

            //sound indication
            SwitchListItem(
                modifier = Modifier.testTag(TestTag.SoundIndicationEnabled),
                text = MR.strings.wakeWordSoundIndication,
                isChecked = viewModel.isSoundIndicationEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordSoundIndicationEnabled
            )

            SoundIndicationSettingsOverview(viewModel)

        }


    }
}

/**
 * overview page for indication settings
 */
@Composable
private fun SoundIndicationSettingsOverview(viewModel: IndicationSettingsViewModel) {

    //visibility of sounds settings
    SecondaryContent(visible = viewModel.isSoundSettingsVisible.collectAsState().value) {

        Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

            RadioButtonsEnumSelectionList(
                modifier = Modifier.testTag(TestTag.AudioOutputOptions),
                selected = viewModel.soundIndicationOutputOption.collectAsState().value,
                onSelect = viewModel::selectSoundIndicationOutputOption,
                values = viewModel.audioOutputOptionsList
            )

            //opens page for sounds
            val navigation = LocalNavController.current

            //wake sound
            ListElement(
                modifier = Modifier
                    .testTag(IndicationSettingsScreens.WakeIndicationSound)
                    .clickable { navigation.navigate(IndicationSettingsScreens.WakeIndicationSound.name) },
                text = { Text(MR.strings.wakeWord) },
                secondaryText = { Text(text = viewModel.wakeSound.collectAsState().value) }
            )

            //recorded sound
            ListElement(
                modifier = Modifier
                    .testTag(IndicationSettingsScreens.RecordedIndicationSound)
                    .clickable { navigation.navigate(IndicationSettingsScreens.RecordedIndicationSound.name) },
                text = { Text(MR.strings.recordedSound) },
                secondaryText = { Text(text = viewModel.recordedSound.collectAsState().value) }
            )

            //error sound
            ListElement(
                modifier = Modifier
                    .testTag(IndicationSettingsScreens.ErrorIndicationSound)
                    .clickable { navigation.navigate(IndicationSettingsScreens.ErrorIndicationSound.name) },
                text = { Text(MR.strings.errorSound) },
                secondaryText = { Text(text = viewModel.errorSound.collectAsState().value) }
            )
        }

    }

}