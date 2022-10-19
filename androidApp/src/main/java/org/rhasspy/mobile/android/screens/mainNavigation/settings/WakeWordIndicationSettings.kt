package org.rhasspy.mobile.android.screens.mainNavigation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.PageContent
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.viewModels.settings.WakeWordIndicationSettingsViewModel

/**
 * wake word indication settings
 */
@Preview
@Composable
fun WakeWordIndicationSettingsContent(viewModel: WakeWordIndicationSettingsViewModel = viewModel()) {

    PageContent(MR.strings.wakeWordIndication) {

        Column {

            //turn on display
            SwitchListItem(
                text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay,
                isChecked = viewModel.isWakeWordDetectionTurnOnDisplayEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordSoundIndicationEnabled
            )

            //sound indication
            SwitchListItem(
                text = MR.strings.wakeWordSoundIndication,
                isChecked = viewModel.isWakeWordSoundIndicationEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordLightIndicationEnabled
            )

            //light indication
            SwitchListItem(
                text = MR.strings.wakeWordLightIndication,
                isChecked = viewModel.isWakeWordLightIndicationEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordDetectionTurnOnDisplay
            )

        }

    }

}
