package org.rhasspy.mobile.ui.configuration.domains.wake.porcupine

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.list.RadioButtonListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.TonalElevationLevel2
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationUiEvent.PorcupineUiEvent.Change.SelectWakeDomainPorcupineLanguage
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationViewModel

/**
 *  list of porcupine languages
 */
@Composable
fun PorcupineLanguageScreen(viewModel: WakeDomainConfigurationViewModel) {

    val viewState by viewModel.viewState.collectAsState()
    val editData = viewState.editData.wakeWordPorcupineConfigurationData

    ScreenContent(
        modifier = Modifier.testTag(TestTag.PorcupineLanguageScreen),
        title = MR.strings.language.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel2,
    ) {

        val coroutineScope = rememberCoroutineScope()
        val state = rememberLazyListState()
        val selectedIndex = editData.languageOptions.indexOf(editData.porcupineLanguage)

        LaunchedEffect(true) {
            coroutineScope.launch {
                state.scrollToItem(selectedIndex)
            }
        }

        LazyColumn(state = state) {

            items(editData.languageOptions) { option ->

                RadioButtonListItem(
                    modifier = Modifier.testTag(option = option),
                    text = option.text,
                    isChecked = editData.porcupineLanguage == option,
                    onClick = { viewModel.onEvent(SelectWakeDomainPorcupineLanguage(option)) }
                )

                CustomDivider()
            }
        }

    }

}