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
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.ComposableLifecycle
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.elements.toText
import org.rhasspy.mobile.android.content.list.InformationListElement
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewmodel.settings.BackgroundServiceSettingsViewModel

/**
 * background service
 * toggle on/off
 * listElement to disable battery optimization
 */
@Preview
@Composable
fun BackgroundServiceSettingsContent(viewModel: BackgroundServiceSettingsViewModel = get()) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreenType.BackgroundServiceSettings),
        title = MR.strings.background
    ) {

        InformationListElement(text = MR.strings.backgroundServiceInformation)

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