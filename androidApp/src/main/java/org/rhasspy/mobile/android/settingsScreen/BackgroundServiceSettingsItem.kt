package org.rhasspy.mobile.android.settingsScreen

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.ComposableLifecycle
import org.rhasspy.mobile.android.utils.ExpandableListItem
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.toText
import org.rhasspy.mobile.viewModels.SettingsScreenViewModel

@Composable
fun BackgroundServiceItem(viewModel: SettingsScreenViewModel) {

    val isBackgroundEnabled by viewModel.isBackgroundEnabled.collectAsState()

    ExpandableListItem(
        text = MR.strings.background,
        secondaryText = isBackgroundEnabled.toText()
    ) {
        //on oFF
        SwitchListItem(
            text = MR.strings.enableBackground,
            isChecked = isBackgroundEnabled,
            onCheckedChange = viewModel::toggleBackgroundEnabled
        )

        var isBatteryDisabled by rememberSaveable { mutableStateOf(viewModel.isBatteryOptimizationDisabled()) }

        ComposableLifecycle { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isBatteryDisabled = viewModel.isBatteryOptimizationDisabled()
            }
        }

        //background battery optimization on/off
        ListElement(
            modifier = Modifier.clickable { viewModel.onDisableBatteryOptimization() },
            text = { Text(MR.strings.batteryOptimization) },
            secondaryText = { Text(isBatteryDisabled.toText()) },
            trailing = {
                Icon(
                    imageVector = Icons.Filled.BatteryAlert,
                    contentDescription = MR.strings.batteryOptimization
                )
            }
        )
    }
}

@Preview
@Composable
fun BackgroundServiceItemPreview(){
    BackgroundServiceItem(viewModel = SettingsScreenViewModel())
}