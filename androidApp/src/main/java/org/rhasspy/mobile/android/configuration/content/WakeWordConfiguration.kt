package org.rhasspy.mobile.android.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.android.utils.DropDownListWithFileOpen
import org.rhasspy.mobile.android.utils.OutlineButtonListItem
import org.rhasspy.mobile.android.utils.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.utils.SliderListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.TextFieldListItemVisibility
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel


/**
 * Content to configure wake word
 * Drop Down of option
 * porcupine wake word settings
 */
@Preview
@Composable
fun WakeWordConfigurationContent(viewModel: WakeWordConfigurationViewModel = viewModel()) {

    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
            OutlineButtonListItem(
                text = MR.strings.openPicoVoiceConsole,
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