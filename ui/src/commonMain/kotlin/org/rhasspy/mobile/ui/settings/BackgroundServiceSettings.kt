package org.rhasspy.mobile.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalSnackBarHostState
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.toText
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.InformationListElement
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.main.SettingsScreenItemContent
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SettingsScreenDestination.BackgroundServiceSettings
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.Action.DisableBatteryOptimization
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.Change.SetBackgroundServiceSettingsEnabled
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsViewModel

/**
 * background service
 * toggle on/off
 * listElement to disable battery optimization
 */

@Composable
fun BackgroundServiceSettingsContent() {
    val viewModel: BackgroundServiceSettingsViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()

        val snackBarHostState = LocalSnackBarHostState.current
        val snackBarText = viewState.snackBarText?.let { translate(it) }

        LaunchedEffect(snackBarText) {
            snackBarText?.also {
                snackBarHostState.showSnackbar(message = it)
                viewModel.onEvent(ShowSnackBar)
            }
        }

        SettingsScreenItemContent(
            modifier = Modifier.testTag(BackgroundServiceSettings),
            title = MR.strings.background.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {

            InformationListElement(text = MR.strings.backgroundServiceInformation.stable)

            //on oFF
            SwitchListItem(
                modifier = Modifier.testTag(TestTag.EnabledSwitch),
                text = MR.strings.enableBackground.stable,
                isChecked = viewState.isBackgroundServiceEnabled,
                onCheckedChange = { viewModel.onEvent(SetBackgroundServiceSettingsEnabled(it)) }
            )

            //visibility of battery optimization
            AnimatedVisibility(
                enter = expandVertically(),
                exit = shrinkVertically(),
                visible = !viewState.isBatteryOptimizationDeactivationEnabled
            ) {

                //background battery optimization on/off
                ListElement(
                    modifier = Modifier
                        .clickable(onClick = { viewModel.onEvent(DisableBatteryOptimization) })
                        .testTag(TestTag.BatteryOptimization),
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.BatteryAlert,
                            contentDescription = MR.strings.batteryOptimization.stable
                        )
                    },
                    text = {
                        Text(MR.strings.batteryOptimization.stable)
                    },
                    secondaryText = {
                        Text(viewState.isBatteryOptimizationDeactivationEnabled.toText())
                    },
                )

            }

        }
    }

}