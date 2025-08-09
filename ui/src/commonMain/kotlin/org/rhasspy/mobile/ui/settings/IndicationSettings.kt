package org.rhasspy.mobile.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.SecondaryContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.main.SettingsScreenItemContent
import org.rhasspy.mobile.ui.settings.sound.IndicationSoundScreen
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.IndicationSettingsScreenDestination.ErrorIndicationSoundScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.IndicationSettingsScreenDestination.RecordedIndicationSoundScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.IndicationSettingsScreenDestination.WakeIndicationSoundScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SettingsScreenDestination.IndicationSettings
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SelectSoundIndicationOutputOption
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetSoundIndicationEnabled
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetWakeWordDetectionTurnOnDisplay
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetWakeWordLightIndicationEnabled
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.WakeIndicationSoundSettingsViewModel

/**
 * indication sounds
 */

@Composable
fun IndicationWakeScreen() {
    IndicationSoundScreen(
        viewModel = LocalViewModelFactory.current.getViewModel<WakeIndicationSoundSettingsViewModel>(),
        screen = WakeIndicationSoundScreen,
        title = MR.strings.wakeSound.stable
    )
}

@Composable
fun IndicationRecordedScreen() {
    IndicationSoundScreen(
        viewModel = LocalViewModelFactory.current.getViewModel<RecordedIndicationSoundSettingsViewModel>(),
        screen = RecordedIndicationSoundScreen,
        title = MR.strings.recordedSound.stable
    )
}

@Composable
fun IndicationErrorScreen() {
    IndicationSoundScreen(
        viewModel = LocalViewModelFactory.current.getViewModel<ErrorIndicationSoundSettingsViewModel>(),
        screen = ErrorIndicationSoundScreen,
        title = MR.strings.errorSound.stable
    )
}

/**
 * wake word indication settings
 */
@Composable
fun IndicationSettingsOverviewScreen() {
    val viewModel: IndicationSettingsViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()

    Screen(screenViewModel = viewModel) {

        SettingsScreenItemContent(
            modifier = Modifier.testTag(IndicationSettings),
            title = MR.strings.indication.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {

            Card(
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {

                //turn on display
                SwitchListItem(
                    modifier = Modifier.testTag(TestTag.WakeWordDetectionTurnOnDisplay),
                    text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay.stable,
                    isChecked = viewState.isWakeWordDetectionTurnOnDisplayEnabled,
                    onCheckedChange = { viewModel.onEvent(SetWakeWordDetectionTurnOnDisplay(it)) }
                )

                //light indication
                SwitchListItem(
                    modifier = Modifier.testTag(TestTag.WakeWordLightIndicationEnabled),
                    text = MR.strings.wakeWordLightIndication.stable,
                    isChecked = viewState.isWakeWordLightIndicationEnabled,
                    onCheckedChange = { viewModel.onEvent(SetWakeWordLightIndicationEnabled(it)) }
                )

                //sound indication
                SwitchListItem(
                    modifier = Modifier.testTag(TestTag.SoundIndicationEnabled),
                    text = MR.strings.wakeWordAudioIndication.stable,
                    isChecked = viewState.isSoundIndicationEnabled,
                    onCheckedChange = { viewModel.onEvent(SetSoundIndicationEnabled(it)) }
                )

                //visibility of sounds settings
                SecondaryContent(visible = viewState.isSoundIndicationEnabled) {

                    SoundIndicationSettingsOverview(
                        soundIndicationOutputOption = viewState.soundIndicationOutputOption,
                        audioOutputOptionList = viewState.audioOutputOptionList,
                        wakeSound = viewState.wakeSound,
                        recordedSound = viewState.recordedSound,
                        errorSound = viewState.errorSound,
                        onEvent = viewModel::onEvent
                    )

                }

            }

        }
    }
}

/**
 * overview page for indication settings
 */
@Composable
private fun SoundIndicationSettingsOverview(
    soundIndicationOutputOption: AudioOutputOption,
    audioOutputOptionList: ImmutableList<AudioOutputOption>,
    wakeSound: String,
    recordedSound: String,
    errorSound: String,
    onEvent: (IndicationSettingsUiEvent) -> Unit,
) {

    Column(modifier = Modifier.padding(ContentPaddingLevel1)) {

        RadioButtonsEnumSelectionList(
            modifier = Modifier.testTag(TestTag.AudioOutputOptions),
            selected = soundIndicationOutputOption,
            onSelect = { onEvent(SelectSoundIndicationOutputOption(it)) },
            values = audioOutputOptionList
        )

        //opens page for sounds

        //wake sound
        ListElement(
            modifier = Modifier
                .testTag(WakeIndicationSoundScreen)
                .clickable { onEvent(Navigate(WakeIndicationSoundScreen)) },
            text = { Text(MR.strings.wakeWord.stable) },
            secondaryText = { Text(text = wakeSound) }
        )

        //recorded sound
        ListElement(
            modifier = Modifier
                .testTag(RecordedIndicationSoundScreen)
                .clickable { onEvent(Navigate(RecordedIndicationSoundScreen)) },
            text = { Text(MR.strings.recordedSound.stable) },
            secondaryText = { Text(text = recordedSound) }
        )

        //error sound
        ListElement(
            modifier = Modifier
                .testTag(ErrorIndicationSoundScreen)
                .clickable { onEvent(Navigate(ErrorIndicationSoundScreen)) },
            text = { Text(MR.strings.errorSound.stable) },
            secondaryText = { Text(text = errorSound) }
        )

    }

}