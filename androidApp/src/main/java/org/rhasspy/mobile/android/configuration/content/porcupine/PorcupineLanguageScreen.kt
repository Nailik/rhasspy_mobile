package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.CustomDivider
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.list.RadioButtonListItem
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.SelectWakeWordPorcupineLanguage
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.PorcupineViewState

/**
 *  list of porcupine languages
 */
@Composable
fun PorcupineLanguageScreen(
    viewState: PorcupineViewState,
    onAction: (PorcupineUiAction) -> Unit
) {

    Scaffold(
        modifier = Modifier
            .testTag(TestTag.PorcupineLanguageScreen)
            .fillMaxSize(),
        topBar = { AppBar(MR.strings.language.stable) }
    ) { paddingValues ->

        Surface(Modifier.padding(paddingValues)) {

            Column {
                viewState.languageOptions.forEach { option ->

                    val isSelected by remember { derivedStateOf { viewState.porcupineLanguage == option } }

                    RadioButtonListItem(
                        modifier = Modifier.testTag(IOption = option),
                        text = option.text,
                        isChecked = isSelected,
                        onClick = { onAction(SelectWakeWordPorcupineLanguage(option)) }
                    )

                    CustomDivider()
                }
            }

        }

    }
}


/**
 * app bar for the language
 */
@Composable
private fun AppBar(title: StableStringResource) {

    val navigation = LocalNavController.current

    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(
                onClick = navigation::popBackStack,
                modifier = Modifier.testTag(TestTag.AppBarBackButton)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back.stable,
                )
            }
        }
    )

}