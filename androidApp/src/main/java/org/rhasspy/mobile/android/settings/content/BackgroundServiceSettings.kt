package org.rhasspy.mobile.android.settings.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.settings.BackgroundServiceSettingsViewModel

/**
 * background service
 * toggle on/off
 * listElement to disable battery optimization
 */
@Preview
@Composable
fun BackgroundServiceSettingsContent(viewModel: BackgroundServiceSettingsViewModel = viewModel()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreens.BackgroundServiceSettings),
        title = MR.strings.background
    ) {

        //on oFF
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.EnabledSwitch),
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

        //visibility of battery optimization
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.isBatteryOptimizationVisible.collectAsState().value
        ) {

            //background battery optimization on/off
            ListElement(
                modifier = Modifier
                    .clickable(onClick = viewModel::onDisableBatteryOptimization)
                    .testTag(TestTag.BatteryOptimization),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.BatteryAlert,
                        contentDescription = MR.strings.batteryOptimization
                    )
                },
                text = {
                    Text(MR.strings.batteryOptimization)
                },
                secondaryText = {
                    Text(viewModel.isBatteryOptimizationDisabled.collectAsState().value.toText())
                },
            )

        }

    }

}