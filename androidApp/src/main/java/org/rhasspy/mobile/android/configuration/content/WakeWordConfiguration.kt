package org.rhasspy.mobile.android.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel


/**
 * Content to configure wake word
 * Drop Down of option
 * porcupine wake word settings
 */
@Preview
@Composable
fun WakeWordConfigurationContent(viewModel: WakeWordConfigurationViewModel = viewModel()) {

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
 * sensitiy slider
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

            //drop down list of porcupine keyword option, contains option to add item from file manager
            DropDownListWithFileOpen(
                overlineText = { Text(MR.strings.wakeWord) },
                selected = viewModel.wakeWordPorcupineKeywordOption.collectAsState().value,
                values = viewModel.wakeWordPorcupineKeywordOptions.collectAsState().value.toTypedArray(),
                onAdd = viewModel::selectPorcupineWakeWordFile,
                onSelect = viewModel::selectWakeWordPorcupineKeywordOption
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