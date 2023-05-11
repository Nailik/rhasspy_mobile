package org.rhasspy.mobile.android.settings.content

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.content.list.CheckBoxListItem
import org.rhasspy.mobile.android.content.list.InformationListElement
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsViewModel

@Composable
fun AudioFocusSettingsContent() {
    val viewModel: AudioFocusSettingsViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreenType.AudioFocusSettings),
        title = MR.strings.audioFocus.stable
    ) {

        InformationListElement(
            text = MR.strings.audioFocusInformation.stable
        )

        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.AudioFocusOption),
            selected = viewState.audioFocusOption,
            onSelect = { viewModel.onEvent(SelectAudioFocusOption(it)) },
            values = viewState.audioFocusOptions,
            secondaryContentVisible = viewState.audioFocusOption != AudioFocusOption.Disabled
        ) {

            AudioFocusSettings(
                isAudioFocusOnNotification = viewState.isAudioFocusOnNotification,
                isAudioFocusOnSound = viewState.isAudioFocusOnSound,
                isAudioFocusOnRecord = viewState.isAudioFocusOnRecord,
                isAudioFocusOnDialog = viewState.isAudioFocusOnDialog,
                onEvent = viewModel::onEvent
            )

        }

    }
}

@Composable
private fun AudioFocusSettings(
    isAudioFocusOnNotification: Boolean,
    isAudioFocusOnSound: Boolean,
    isAudioFocusOnRecord: Boolean,
    isAudioFocusOnDialog: Boolean,
    onEvent: (AudioFocusSettingsUiEvent) -> Unit
) {

    Column(modifier = Modifier.testTag(TestTag.AudioFocusSettingsConfiguration)) {

        CheckBoxListItem(
            modifier = Modifier.testTag(TestTag.AudioFocusOnNotification),
            text = MR.strings.onNotification.stable,
            isChecked = isAudioFocusOnNotification,
            onCheckedChange = { onEvent(SetAudioFocusOnNotification(it)) }
        )

        CheckBoxListItem(
            modifier = Modifier.testTag(TestTag.AudioFocusOnSound),
            text = MR.strings.onSound.stable,
            secondaryText = MR.strings.onSoundInformation.stable,
            isChecked = isAudioFocusOnSound,
            onCheckedChange = { onEvent(SetAudioFocusOnSound(it)) }
        )

        CheckBoxListItem(
            modifier = Modifier.testTag(TestTag.AudioFocusOnRecord),
            text = MR.strings.onRecord.stable,
            secondaryText = MR.strings.onRecordInformation.stable,
            isChecked = isAudioFocusOnRecord,
            onCheckedChange = { onEvent(SetAudioFocusOnRecord(it)) }
        )

        CheckBoxListItem(
            modifier = Modifier.testTag(TestTag.AudioFocusOnDialog),
            text = MR.strings.onDialog.stable,
            secondaryText = MR.strings.onDialogInformation.stable,
            isChecked = isAudioFocusOnDialog,
            onCheckedChange = { onEvent(SetAudioFocusOnDialog(it)) }
        )

    }
}