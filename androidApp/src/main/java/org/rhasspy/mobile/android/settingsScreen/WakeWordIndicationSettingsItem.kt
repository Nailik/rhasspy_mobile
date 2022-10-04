package org.rhasspy.mobile.android.settingsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.ExpandableListItemString
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

@Composable
fun WakeWordIndicationItem(viewModel: SettingsScreenViewModel) {
    val isWakeWordSoundIndication by viewModel.isWakeWordSoundIndicationEnabled.collectAsState()
    val isWakeWordLightIndication by viewModel.isWakeWordLightIndicationEnabled.collectAsState()

    var stateText = if (isWakeWordSoundIndication) translate(MR.strings.sound) else ""
    if (isWakeWordLightIndication) {
        if (stateText.isNotEmpty()) {
            stateText += " ${translate(MR.strings._and)} "
        }
        stateText += translate(MR.strings.light)
    }
    if (stateText.isEmpty()) {
        stateText = translate(MR.strings.disabled)
    }

    ExpandableListItemString(
        text = MR.strings.wakeWordIndication,
        secondaryText = stateText
    ) {
        Column {

            SwitchListItem(
                text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay,
                isChecked = viewModel.isBackgroundWakeWordDetectionTurnOnDisplay.collectAsState().value,
                onCheckedChange = viewModel::updateWakeWordSoundIndicationEnabled
            )

            SwitchListItem(
                text = MR.strings.wakeWordSoundIndication,
                isChecked = isWakeWordSoundIndication,
                onCheckedChange = viewModel::updateWakeWordLightIndicationEnabled
            )

            SwitchListItem(
                text = MR.strings.wakeWordLightIndication,
                isChecked = isWakeWordLightIndication,
                onCheckedChange = viewModel::updateBackgroundWakeWordDetectionTurnOnDisplay
            )
        }
    }
}
