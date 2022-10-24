package org.rhasspy.mobile.android.settings.content

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.ComposableLifecycle
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.android.utils.PageContent
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.toText
import org.rhasspy.mobile.viewModels.settings.BackgroundServiceSettingsViewModel

/**
 * background service
 * toggle on/off
 * listElement to disable battery optimization
 */
@Preview
@Composable
fun BackgroundServiceSettingsContent(viewModel: BackgroundServiceSettingsViewModel = viewModel()) {

    PageContent(MR.strings.background) {

        //on oFF
        SwitchListItem(
            text = MR.strings.enableBackground,
            isChecked = viewModel.isBackgroundServiceEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleBackgroundServiceEnabled
        )

        //tell viewModel when ui is resumed
        ComposableLifecycle { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onResume()
            }
        }

        //background battery optimization on/off
        ListElement(
            modifier = Modifier.clickable(onClick = viewModel::onDisableBatteryOptimization),
            text = {
                Text(MR.strings.batteryOptimization)
            },
            secondaryText = {
                Text(viewModel.isBatteryOptimizationDisabled.value.toText())
            },
            trailing = {
                Icon(
                    imageVector = Icons.Filled.BatteryAlert,
                    contentDescription = MR.strings.batteryOptimization
                )
            }
        )

    }

}