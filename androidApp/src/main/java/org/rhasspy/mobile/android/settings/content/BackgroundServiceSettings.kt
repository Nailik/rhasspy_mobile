package org.rhasspy.mobile.android.settings.content

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
import androidx.compose.ui.tooling.preview.Preview
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.elements.toText
import org.rhasspy.mobile.android.content.elements.translate
import org.rhasspy.mobile.android.content.list.InformationListElement
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.main.LocalSnackbarHostState
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceSettingsViewModel
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.Action.DisableBatteryOptimization
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.Change.SetBackgroundServiceEnabled
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent.Consumed.ShowSnackBar

/**
 * background service
 * toggle on/off
 * listElement to disable battery optimization
 */
@Preview
@Composable
fun BackgroundServiceSettingsContent() {
    val viewModel: BackgroundServiceSettingsViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()

    val snackBarHostState = LocalSnackbarHostState.current
    val snackBarText = viewState.snackBarText?.let { translate(it) }

    LaunchedEffect(snackBarText) {
        snackBarText?.also {
            snackBarHostState.showSnackbar(message = it)
            viewModel.onEvent(ShowSnackBar)
        }
    }

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreenType.BackgroundServiceSettings),
        title = MR.strings.background.stable
    ) {

        InformationListElement(text = MR.strings.backgroundServiceInformation.stable)

        //on oFF
        SwitchListItem(
            modifier = Modifier.testTag(TestTag.EnabledSwitch),
            text = MR.strings.enableBackground.stable,
            isChecked = viewState.isBackgroundServiceEnabled,
            onCheckedChange = { viewModel.onEvent(SetBackgroundServiceEnabled(it)) }
        )

        //visibility of battery optimization
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = !viewState.isBatteryOptimizationDisabled
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
                    Text(viewState.isBatteryOptimizationDisabled.toText())
                },
            )

        }

    }

}