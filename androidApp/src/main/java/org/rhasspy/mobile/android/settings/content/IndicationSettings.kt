package org.rhasspy.mobile.android.settings.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreens
import org.rhasspy.mobile.android.settings.content.sound.IndicationSettingsScreens
import org.rhasspy.mobile.android.settings.content.sound.IndicationSoundScreen
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.viewModels.settings.IndicationSettingsViewModel
import org.rhasspy.mobile.viewModels.settings.sound.ErrorIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewModels.settings.sound.RecordedIndicationSoundSettingsViewModel
import org.rhasspy.mobile.viewModels.settings.sound.WakeIndicationSoundSettingsViewModel

@Preview
@Composable
fun WakeWordIndicationSettingsContent(viewModel: IndicationSettingsViewModel = viewModel()) {

    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = IndicationSettingsScreens.Overview.name
        ) {

            composable(IndicationSettingsScreens.Overview.name) {
                IndicationSettingsOverview(viewModel)
            }

            composable(IndicationSettingsScreens.WakeIndicationSound.name) {
                IndicationSoundScreen(
                    viewModel = WakeIndicationSoundSettingsViewModel(),
                    title = MR.strings.wakeSound,
                    screen = IndicationSettingsScreens.WakeIndicationSound
                )
            }

            composable(IndicationSettingsScreens.RecordedIndicationSound.name) {
                IndicationSoundScreen(
                    viewModel = RecordedIndicationSoundSettingsViewModel(),
                    title = MR.strings.recordedSound,
                    screen = IndicationSettingsScreens.RecordedIndicationSound
                )
            }

            composable(IndicationSettingsScreens.ErrorIndicationSound.name) {
                IndicationSoundScreen(
                    viewModel = ErrorIndicationSoundSettingsViewModel(),
                    title = MR.strings.errorSound,
                    screen = IndicationSettingsScreens.ErrorIndicationSound
                )
            }

        }
    }

}

/**
 * wake word indication settings
 */
@Composable
fun IndicationSettingsOverview(viewModel: IndicationSettingsViewModel) {

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreens.IndicationSettings),
        title = MR.strings.indication
    ) {

        Card(
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {

            //turn on display
            SwitchListItem(
                text = MR.strings.backgroundWakeWordDetectionTurnOnDisplay,
                isChecked = viewModel.isWakeWordDetectionTurnOnDisplayEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordDetectionTurnOnDisplay
            )

            //light indication
            SwitchListItem(
                text = MR.strings.wakeWordLightIndication,
                isChecked = viewModel.isWakeWordLightIndicationEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordLightIndicationEnabled
            )

            //sound indication
            SwitchListItem(
                text = MR.strings.wakeWordSoundIndication,
                isChecked = viewModel.isSoundIndicationEnabled.collectAsState().value,
                onCheckedChange = viewModel::toggleWakeWordSoundIndicationEnabled
            )

            SoundIndicationSettingsOverview(viewModel)

        }


    }
}


@Composable
fun SoundIndicationSettingsOverview(viewModel: IndicationSettingsViewModel) {
    //visibility of sounds settings
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.isSoundSettingsVisible.collectAsState().value
    ) {


        Card(
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {

            //opens page for sounds
            val navigation = LocalNavController.current

            //wake sound
            ListElement(
                modifier = Modifier
                    .testTag(IndicationSettingsScreens.WakeIndicationSound)
                    .clickable { navigation.navigate(IndicationSettingsScreens.WakeIndicationSound.name) },
                text = { Text(MR.strings.wakeWord) },
                secondaryText = { Text(text = viewModel.wakeSound.collectAsState().value) }
            )

            //recorded sound
            ListElement(
                modifier = Modifier
                    .testTag(IndicationSettingsScreens.RecordedIndicationSound)
                    .clickable { navigation.navigate(IndicationSettingsScreens.RecordedIndicationSound.name) },
                text = { Text(MR.strings.recordedSound) },
                secondaryText = { Text(text = viewModel.recordedSound.collectAsState().value) }
            )

            //error sound
            ListElement(
                modifier = Modifier
                    .testTag(IndicationSettingsScreens.ErrorIndicationSound)
                    .clickable { navigation.navigate(IndicationSettingsScreens.ErrorIndicationSound.name) },
                text = { Text(MR.strings.errorSound) },
                secondaryText = { Text(text = viewModel.errorSound.collectAsState().value) }
            )


            val navController = LocalMainNavController.current

            ListElement(
                modifier = Modifier
                    .clickable {
                        //when destination is already in backstack, backstack to destination
                        if (navController.backQueue.lastOrNull { entry -> entry.destination.route == ConfigurationScreens.AudioPlayingConfiguration.name } != null) {
                            navController.popBackStack(
                                route = ConfigurationScreens.AudioPlayingConfiguration.name,
                                inclusive = false
                            )
                        } else {
                            navController.navigate(ConfigurationScreens.AudioPlayingConfiguration.name)
                        }
                    }
                    .testTag(ConfigurationScreens.AudioPlayingConfiguration),
                icon = { Icon(Icons.Filled.Info, contentDescription = MR.strings.info) },
                text = { Text(MR.strings.audioPlaying) },
                overlineText = { Text(viewModel.audioPlayingOption.collectAsState().value.name) },
                secondaryText = { Text(MR.strings.audioPlayingInfo) }
            )

        }
    }
}