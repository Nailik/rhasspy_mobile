package org.rhasspy.mobile.android.settings.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelectionList
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.android.permissions.RequiresOverlayPermission
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.list.SwitchListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.MicrophoneOverlaySettings
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SelectMicrophoneOverlaySizeOption
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SetMicrophoneOverlayWhileAppEnabled
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsViewModel

/**
 * Settings vor Microphone overlay
 *
 * overlay on/off
 * overlay while app is opened on/off
 */
@Preview
@Composable
fun MicrophoneOverlaySettingsContent() {
    val viewModel: MicrophoneOverlaySettingsViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(viewModel) {
        SettingsScreenItemContent(
            modifier = Modifier.testTag(MicrophoneOverlaySettings),
            title = MR.strings.microphoneOverlay.stable,
            onBackClick = { viewModel.onEvent(BackClick) }
        ) {
            Column {

                val viewState by viewModel.viewState.collectAsState()

                Card(
                    modifier = Modifier.padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column {

                        //overlay permission request
                        RequiresOverlayPermission(
                            initialData = viewState.microphoneOverlaySizeOption,
                            onClick = { viewModel.onEvent(SelectMicrophoneOverlaySizeOption(it)) }
                        ) { onClick ->

                            //drop down to select option
                            RadioButtonsEnumSelectionList(
                                modifier = Modifier.testTag(TestTag.MicrophoneOverlaySizeOptions),
                                selected = viewState.microphoneOverlaySizeOption,
                                onSelect = { option ->
                                    if (option != MicrophoneOverlaySizeOption.Disabled) {
                                        //invoke permission request
                                        onClick.invoke(option)
                                    } else {
                                        //doesn't invoke permission request
                                        viewModel.onEvent(SelectMicrophoneOverlaySizeOption(option))
                                    }
                                },
                                values = viewState.microphoneOverlaySizeOptions
                            )

                        }

                    }

                    val showOverlayWhileAppEnabled by remember { derivedStateOf { viewState.microphoneOverlaySizeOption != MicrophoneOverlaySizeOption.Disabled } }

                    //visibility of overlay while app
                    AnimatedVisibility(
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                        visible = showOverlayWhileAppEnabled
                    ) {

                        SwitchListItem(
                            modifier = Modifier.testTag(TestTag.VisibleWhileAppIsOpened),
                            text = MR.strings.whileAppIsOpened.stable,
                            isChecked = viewState.isMicrophoneOverlayWhileAppEnabled,
                            onCheckedChange = { viewModel.onEvent(SetMicrophoneOverlayWhileAppEnabled(it)) }
                        )

                    }

                }

            }
        }
    }

}