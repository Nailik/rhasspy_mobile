package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.configuration.content.porcupine.PorcupineKeywordScreen
import org.rhasspy.mobile.android.configuration.content.porcupine.PorcupineLanguageScreen
import org.rhasspy.mobile.android.configuration.test.EventListItem
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.ContentPaddingLevel1
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel

private enum class WakeWordConfigurationScreens {
    Overview,
    PorcupineKeyword,
    PorcupineLanguage
}

/**
 * Nav Host of Wake word configuration screens
 */
@Preview
@Composable
fun WakeWordConfigurationContent(viewModel: WakeWordConfigurationViewModel = viewModel()) {

    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = WakeWordConfigurationScreens.Overview.name
        ) {

            composable(WakeWordConfigurationScreens.Overview.name) {
                WakeWordConfigurationOverview(viewModel)
            }

            composable(WakeWordConfigurationScreens.PorcupineKeyword.name) {
                PorcupineKeywordScreen(viewModel)
            }

            composable(WakeWordConfigurationScreens.PorcupineLanguage.name) {
                PorcupineLanguageScreen(viewModel)
            }

        }
    }

}

/**
 * Overview to configure wake word
 * Drop Down of option
 * porcupine wake word settings
 */
@Composable
private fun WakeWordConfigurationOverview(viewModel: WakeWordConfigurationViewModel) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.WakeWordConfiguration),
        title = MR.strings.wakeWord,
        viewModel = viewModel,
        testContent = { modifier -> TestContent(modifier, viewModel) }
    ) {

        //drop down list to select option
        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.WakeWordOptions),
            selected = viewModel.wakeWordOption.collectAsState().value,
            onSelect = viewModel::selectWakeWordOption,
            values = viewModel.wakeWordOptions
        ) {

            if (viewModel.isWakeWordPorcupineSettingsVisible(it)) {
                PorcupineConfiguration(viewModel)
            }

        }
    }

}

/**
 * configuration for porcupine hot word
 * picovoice console for token
 * file option
 * language selection
 * sensitivity slider
 */
@Composable
private fun PorcupineConfiguration(viewModel: WakeWordConfigurationViewModel) {

    Column(
        modifier = Modifier
            .testTag(TestTag.PorcupineWakeWordSettings)
            .padding(ContentPaddingLevel1)
    ) {

        //porcupine access token
        TextFieldListItemVisibility(
            modifier = Modifier.testTag(TestTag.PorcupineAccessToken),
            value = viewModel.wakeWordPorcupineAccessToken.collectAsState().value,
            onValueChange = viewModel::updateWakeWordPorcupineAccessToken,
            label = MR.strings.porcupineAccessKey
        )

        //button to open picovoice console to generate access token
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineOpenConsole)
                .clickable(onClick = viewModel::openPicoVoiceConsole),
            icon = {
                Icon(
                    imageVector = Icons.Filled.Cloud,
                    contentDescription = MR.strings.openPicoVoiceConsole
                )
            },
            text = { Text(MR.strings.openPicoVoiceConsole) },
            secondaryText = { Text(MR.strings.openPicoVoiceConsoleInfo) }
        )

        //opens page for porcupine keyword selection
        val navigation = LocalNavController.current

        //wake word list
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineKeyword)
                .clickable { navigation.navigate(WakeWordConfigurationScreens.PorcupineKeyword.name) },
            text = { Text(MR.strings.wakeWord) },
            secondaryText = { Text("${viewModel.wakeWordPorcupineKeywordCount.collectAsState().value} ${translate(MR.strings.active)}") }
        )

        //opens page for porcupine language selection
        ListElement(
            modifier = Modifier
                .testTag(TestTag.PorcupineLanguage)
                .clickable { navigation.navigate(WakeWordConfigurationScreens.PorcupineLanguage.name) },
            text = { Text(MR.strings.language) },
            secondaryText = { Text(viewModel.wakeWordPorcupineLanguage.collectAsState().value.text) }
        )

    }

}

@Composable
private fun TestContent(
    modifier: Modifier,
    viewModel: WakeWordConfigurationViewModel
) {

    val eventsList by viewModel.events.collectAsState()

    LazyColumn(modifier = modifier.fillMaxHeight()) {
        items(eventsList) { item ->
            EventListItem(item)
        }
    }
}