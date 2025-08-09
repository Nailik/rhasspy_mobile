package org.rhasspy.mobile.ui.configuration.porcupine

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.RadioButtonListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.SelectWakeWordPorcupineLanguage
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel

/**
 *  list of porcupine languages
 */
@Composable
fun PorcupineLanguageScreen() {
    val viewModel: WakeWordConfigurationViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()
    val editData = viewState.editData.wakeWordPorcupineConfigurationData

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            AppBar(
                title = MR.strings.language.stable,
                onEvent = viewModel::onEvent
            )
        }
    ) { paddingValues ->

        Surface(
            Modifier.padding(paddingValues)
                .testTag(TestTag.PorcupineLanguageScreen)
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
                        onClick = { viewModel.onEvent(SelectWakeWordPorcupineLanguage(option)) }
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    title: StableStringResource,
    onEvent: (PorcupineUiEvent) -> Unit
) {

    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(
                onClick = { onEvent(BackClick) },
                modifier = Modifier.testTag(TestTag.AppBarBackButton)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = MR.strings.back.stable,
                )
            }
        }
    )

}