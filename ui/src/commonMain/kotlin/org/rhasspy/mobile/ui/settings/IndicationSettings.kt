package org.rhasspy.mobile.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.TonalElevationLevel1
import org.rhasspy.mobile.ui.theme.TonalElevationLevel3
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SettingsScreenDestination.IndicationSettings
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetWakeWordDetectionTurnOnDisplay
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetWakeWordLightIndicationEnabled
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewModel


/**
 * wake word indication settings
 */
@Composable
fun IndicationSettingsOverviewScreen(viewModel: IndicationSettingsViewModel) {

    val viewState by viewModel.viewState.collectAsState()


    ScreenContent(
        modifier = Modifier.testTag(IndicationSettings),
        title = MR.strings.indication.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel1,
    ) {

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = TonalElevationLevel3)
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

        }


    }

}
