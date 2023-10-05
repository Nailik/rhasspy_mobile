package org.rhasspy.mobile.ui.configuration.domains.wake.porcupine

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.RadioButtonListItem
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationUiEvent.PorcupineUiEvent
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationUiEvent.PorcupineUiEvent.Action.BackClick
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
        screenViewModel = viewModel
    ) {

        Surface(tonalElevation = 3.dp) {
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
                                onClick = { viewModel.onEvent(SelectWakeDomainPorcupineLanguage(option)) }
                            )

                            CustomDivider()
                        }
                    }

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
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back.stable,
                )
            }
        }
    )

}