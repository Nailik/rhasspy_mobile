package org.rhasspy.mobile.ui.settings.content

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
import org.rhasspy.mobile.ui.content.SecondaryContent
import org.rhasspy.mobile.ui.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.main.LocalViewModelFactory
import org.rhasspy.mobile.ui.permissions.RequiresOverlayPermission
import org.rhasspy.mobile.ui.settings.SettingsScreenItemContent
import org.rhasspy.mobile.ui.settings.content.sound.IndicationSoundScreen
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.ContentPaddingLevel1
import org.rhasspy.mobile.viewmodel.navigation.Screen.SettingsScreen.IndicationSettings
import org.rhasspy.mobile.viewmodel.navigation.Screen.SettingsScreen.IndicationSettings.*
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Navigate.*
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewState
import org.rhasspy.mobile.viewmodel.settings.indication.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.indication.sound.WakeIndicationSoundSettingsViewModel

/**
 * indication sounds
 */
@Composable
fun IndicationSettingsContent(screen: IndicationSettings) {
    val viewModelFactory = LocalViewModelFactory.current
    val viewModel: IndicationSettingsViewModel = viewModelFactory.getViewModel()

    when (screen) {
        Overview -> {
            val viewState by viewModel.viewState.collectAsState()
            IndicationSettingsOverview(
                viewState = viewState,
                onEvent = viewModel::onEvent
            )
        }

        ErrorIndicationSound -> IndicationSoundScreen(
            viewModel = viewModelFactory.getViewModel<ErrorIndicationSoundSettingsViewModel>(),
            title = MR.strings.errorSound.stable
        )

        RecordedIndicationSound -> IndicationSoundScreen(
            viewModel = viewModelFactory.getViewModel<RecordedIndicationSoundSettingsViewModel>(),
            title = MR.strings.recordedSound.stable
        )

        WakeIndicationSound -> IndicationSoundScreen(
            viewModel = viewModelFactory.getViewModel<WakeIndicationSoundSettingsViewModel>(),
            title = MR.strings.wakeSound.stable
        )
    }
}

/**
 * wake word indication settings
 */
@Composable
fun IndicationSettingsOverview(
    viewState: IndicationSettingsViewState,
    onEvent: (IndicationSettingsUiEvent) -> Unit
) {

    SettingsScreenItemContent(
        modifier = Modifier,
        title = MR.strings.indication.stable,
        onBackClick = {}
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
                onCheckedChange = { onEvent(SetWakeWordDetectionTurnOnDisplay(it)) }
            )

            //light indication
            RequiresOverlayPermission(
                initialData = viewState.isWakeWordLightIndicationEnabled,
                onClick = { onEvent(SetWakeWordLightIndicationEnabled(it)) }
            ) { onClick ->
                SwitchListItem(
                    modifier = Modifier.testTag(TestTag.WakeWordLightIndicationEnabled),
                    text = MR.strings.wakeWordLightIndication.stable,
                    isChecked = viewState.isWakeWordLightIndicationEnabled,
                    onCheckedChange = onClick
                )
            }

            //sound indication
            SwitchListItem(
                modifier = Modifier.testTag(TestTag.SoundIndicationEnabled),
                text = MR.strings.wakeWordSoundIndication.stable,
                isChecked = viewState.isSoundIndicationEnabled,
                onCheckedChange = { onEvent(SetSoundIndicationEnabled(it)) }
            )


            //visibility of sounds settings
            SecondaryContent(visible = viewState.isSoundIndicationEnabled) {

                SoundIndicationSettingsOverview(
                    soundIndicationOutputOption = viewState.soundIndicationOutputOption,
                    audioOutputOptionList = viewState.audioOutputOptionList,
                    wakeSound = viewState.wakeSound,
                    recordedSound = viewState.recordedSound,
                    errorSound = viewState.errorSound,
                    onEvent = onEvent
                )

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
    onEvent: (IndicationSettingsUiEvent) -> Unit
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
                .clickable { onEvent(WakeIndicationSoundClick) },
            text = { Text(MR.strings.wakeWord.stable) },
            secondaryText = { Text(text = wakeSound) }
        )

        //recorded sound
        ListElement(
            modifier = Modifier
                .clickable { onEvent(RecordedIndicationSoundClick) },
            text = { Text(MR.strings.recordedSound.stable) },
            secondaryText = { Text(text = recordedSound) }
        )

        //error sound
        ListElement(
            modifier = Modifier
                .clickable { onEvent(ErrorIndicationSoundClick) },
            text = { Text(MR.strings.errorSound.stable) },
            secondaryText = { Text(text = errorSound) }
        )

    }

}