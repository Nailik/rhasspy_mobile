package org.rhasspy.mobile.android.configuration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.permissions.MicrophonePermissionInfoDialog
import org.rhasspy.mobile.android.screens.ConfigurationScreens
import org.rhasspy.mobile.android.screens.LocalSnackbarHostState
import org.rhasspy.mobile.android.utils.ConfigurationListContent
import org.rhasspy.mobile.android.utils.ConfigurationListItem
import org.rhasspy.mobile.android.utils.DropDownEnumListItem
import org.rhasspy.mobile.android.utils.DropDownListWithFileOpen
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.OutlineButtonListItem
import org.rhasspy.mobile.android.utils.SliderListItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel

/**
 * List element for wake wordc onfiguration
 * shows which option is selected
 */
@Composable
fun WakeWordConfigurationItem(viewModel: ConfigurationScreenViewModel) {

    val wakeWordValueOption = viewModel.wakeWordOption.flow.collectAsState().value

    ConfigurationListItem(
        text = MR.strings.wakeWord,
        secondaryText = wakeWordValueOption.text,
        screen = ConfigurationScreens.WakeWord
    )
}

/**
 * Content to configure wake word
 * Drop Down of option
 * porcupine wake word settings
 */
@Composable
fun WakeWordConfigurationContent(viewModel: ConfigurationScreenViewModel) {

    ConfigurationListContent(MR.strings.wakeWord) {

        val snackbarHostState = LocalSnackbarHostState.current
        val snackBarMessage = translate(MR.strings.microphonePermissionDenied)
        val snackBarActionLabel = translate(MR.strings.settings)

        if (viewModel.isWakeWordMicrophonePermissionRequestDialogShown.collectAsState().value) {
            //show dialog
            MicrophonePermissionInfoDialog(MR.strings.microphonePermissionInfoWakeWord) { result ->

                //send dialog result to view model
                viewModel.wakeWordMicrophonePermissionRequestDialogResult(result) {

                    //eventually display snackbar
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = snackBarMessage,
                        actionLabel = snackBarActionLabel,
                        duration = SnackbarDuration.Short,
                    )

                    //if button to show permission request was clicked
                    viewModel.wakeWordMicrophonePermissionRequestSnackbarResult(snackbarResult == SnackbarResult.ActionPerformed)
                }
            }
        }

        DropDownEnumListItem(
            selected = viewModel.wakeWordOption.flow.collectAsState().value,
            onSelect = viewModel.wakeWordOption::set,
            values = WakeWordOption::values
        )

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
private fun PorcupineConfiguration(viewModel: ConfigurationScreenViewModel) {
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.wakeWordOption.flow.collectAsState().value == WakeWordOption.Porcupine
    ) {

        Column {

            var isShowAccessToken by rememberSaveable { mutableStateOf(false) }

            TextFieldListItem(
                value = viewModel.wakeWordPorcupineAccessToken.flow.collectAsState().value,
                onValueChange = viewModel.wakeWordPorcupineAccessToken::set,
                label = MR.strings.porcupineAccessKey,
                visualTransformation = if (isShowAccessToken) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isShowAccessToken = !isShowAccessToken }) {
                        Icon(
                            imageVector = if (isShowAccessToken) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = MR.strings.visibility,
                        )
                    }
                },
            )

            OutlineButtonListItem(
                text = MR.strings.openPicoVoiceConsole,
                onClick = viewModel::openPicoVoiceConsole
            )

            //filled with correct values later
            DropDownListWithFileOpen(
                overlineText = { Text(MR.strings.wakeWord) },
                selected = viewModel.wakeWordPorcupineKeywordOption.flow.collectAsState().value,
                values = viewModel.wakeWordPorcupineKeywordOptions.flow.collectAsState().value.toTypedArray(),
                onAdd = viewModel::selectPorcupineWakeWordFile,
                onSelect = viewModel.wakeWordPorcupineKeywordOption::set
            )

            DropDownEnumListItem(
                selected = viewModel.wakeWordPorcupineLanguage.flow.collectAsState().value,
                onSelect = viewModel.wakeWordPorcupineLanguage::set,
                values = PorcupineLanguageOptions::values
            )

            SliderListItem(
                text = MR.strings.sensitivity,
                value = viewModel.wakeWordPorcupineKeywordSensitivity.flow.collectAsState().value,
                onValueChange = viewModel.wakeWordPorcupineKeywordSensitivity::set
            )
        }
    }
}