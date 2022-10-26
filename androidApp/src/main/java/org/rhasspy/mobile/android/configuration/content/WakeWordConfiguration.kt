package org.rhasspy.mobile.android.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import org.rhasspy.mobile.android.main.*
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel
import java.util.*

private enum class WakeWordConfigurationScreens {
    Overview,
    Keyword
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
            composable(WakeWordConfigurationScreens.Keyword.name) {
                WakeWordKeywordScreen(viewModel)
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
        hasUnsavedChanges = viewModel.hasUnsavedChanges,
        onSave = viewModel::save,
        onTest = viewModel::test,
        onDiscard = viewModel::discard
    ) {

        //drop down list to select option
        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.WakeWordOptions),
            selected = viewModel.wakeWordOption.collectAsState().value,
            onSelect = viewModel::selectWakeWordOption,
            values = viewModel.wakeWordOptions
        )

        //porcupine settings
        PorcupineConfiguration(viewModel)
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

    //visibility of porcupine settings
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.wakeWordPorcupineSettingsVisible.collectAsState().value
    ) {

        Column {

            //porcupine access token
            TextFieldListItemVisibility(
                value = viewModel.wakeWordPorcupineAccessToken.collectAsState().value,
                onValueChange = viewModel::updateWakeWordPorcupineAccessToken,
                label = MR.strings.porcupineAccessKey
            )

            //button to open picovoice console to generate access token
            FilledTonalIconButtonListItem(
                text = MR.strings.openPicoVoiceConsole,
                imageVector = Icons.Filled.OpenInNew,
                onClick = viewModel::openPicoVoiceConsole
            )

            //opens page for keyword selection
            val navigation = LocalNavController.current

            val selectedOption = viewModel.wakeWordPorcupineKeywordOptions.collectAsState().value
                .elementAt(viewModel.wakeWordPorcupineKeywordOption.collectAsState().value)

            ListElement(
                modifier = Modifier.clickable { navigation.navigate(WakeWordConfigurationScreens.Keyword.name) },
                text = { Text(MR.strings.wakeWord) },
                secondaryText = { Text(selectedOption.lowercase().replaceFirstChar { it.uppercaseChar() }) }
            )

            //porcupine language dropdown
            DropDownEnumListItem(
                label = MR.strings.language,
                selected = viewModel.wakeWordPorcupineLanguage.collectAsState().value,
                onSelect = viewModel::selectWakeWordPorcupineLanguage,
                values = viewModel.porcupineLanguageOptions
            )

            //sensitivity of porcupine
            SliderListItem(
                text = MR.strings.sensitivity,
                value = viewModel.wakeWordPorcupineSensitivity.collectAsState().value,
                onValueChange = viewModel::updateWakeWordPorcupineSensitivity
            )

        }

    }

}

/**
 *  list of porcupine keyword option, contains option to add item from file manager
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WakeWordKeywordScreen(viewModel: WakeWordConfigurationViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { KeywordAppBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::selectPorcupineWakeWordFile) {
                Icon(imageVector = Icons.Filled.FileOpen, contentDescription = MR.strings.fileOpen)
            }
        },
    ) { paddingValues ->
        Surface(Modifier.padding(paddingValues)) {

            val selectedOption = viewModel.wakeWordPorcupineKeywordOptions.collectAsState().value
                .elementAt(viewModel.wakeWordPorcupineKeywordOption.collectAsState().value)

            val options by viewModel.wakeWordPorcupineKeywordOptions.collectAsState()

            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(count = options.size,
                    itemContent = { index ->

                        val element = options.elementAt(index)

                        RadioButtonListItem(
                            text = element.lowercase().replaceFirstChar { it.uppercaseChar() },
                            isChecked = element == selectedOption,
                            onClick = { viewModel.selectWakeWordPorcupineKeywordOption(index) })

                        CustomDivider()
                })
            }
        }
    }
}

/**
 * app bar for the keyword
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KeywordAppBar() {

    val navigation = LocalNavController.current

    TopAppBar(
        title = {
            Text(MR.strings.wakeWord)
        },
        navigationIcon = {
            IconButton(
                onClick = navigation::popBackStack,
                modifier = Modifier.testTag(TestTag.AppBarBackButton)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back,
                )
            }
        }
    )

}