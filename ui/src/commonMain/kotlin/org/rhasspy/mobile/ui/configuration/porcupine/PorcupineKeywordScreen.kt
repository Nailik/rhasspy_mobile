package org.rhasspy.mobile.ui.configuration.porcupine

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.theme.horizontalAnimationSpec
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel

/**
 *  screen for porcupine keyword option
 *  page with default options
 *  page with custom options
 *  bottom bar to switch between pages
 */
@Composable
fun PorcupineKeywordScreen() {
    val viewModel: WakeWordConfigurationViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()
    val editData = viewState.editData.wakeWordPorcupineConfigurationData

    Surface(tonalElevation = 3.dp) {
        Scaffold(
            modifier = Modifier
                .testTag(TestTag.PorcupineKeywordScreen)
                .fillMaxSize(),
            topBar = {
                Column {
                    AppBar(viewModel::onEvent)
                    //opens page for porcupine language selection
                    ListElement(
                        modifier = Modifier
                            .testTag(TestTag.PorcupineLanguage)
                            .clickable { viewModel.onEvent(PorcupineLanguageClick) },
                        text = { Text(MR.strings.language.stable) },
                        secondaryText = { Text(editData.porcupineLanguage.text) }
                    )
                }
            },
            bottomBar = {
                Surface(tonalElevation = 3.dp) {
                    //bottom tab bar with pages tabs
                    BottomTabBar(
                        selectedIndex = viewState.porcupineWakeWordScreen,
                        onSelectedScreen = { viewModel.onEvent(PageClick(it)) }
                    )
                }
            }

        ) { paddingValues ->
            //horizontal pager to slide between pages
            Surface(modifier = Modifier.padding(paddingValues)) {

                AnimatedContent(
                    targetState = viewState.porcupineWakeWordScreen,
                    transitionSpec = {
                        horizontalAnimationSpec(targetState, initialState)
                    }
                ) { targetState ->
                    when (targetState) {
                        0 -> PorcupineKeywordDefaultScreen(
                            editData = editData,
                            onEvent = viewModel::onEvent
                        )

                        1 -> PorcupineKeywordCustomScreen(
                            editData = editData,
                            onEvent = viewModel::onEvent
                        )
                    }
                }

            }
        }
    }
}


/**
 * app bar for title and back button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(onEvent: (PorcupineUiEvent) -> Unit) {

    TopAppBar(
        title = { Text(MR.strings.porcupineKeyword.stable) },
        navigationIcon = {
            IconButton(
                onClick = { onEvent(BackClick) },
                modifier = Modifier.testTag(TestTag.AppBarBackButton)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back.stable
                )
            }
        }
    )

}


/**
 * Displays tabs on bottom (default/ custom)
 */
@Composable
private fun BottomTabBar(
    selectedIndex: Int,
    onSelectedScreen: (screen: Int) -> Unit
) {

    Column {

        //tab bar row
        TabRow(selectedTabIndex = selectedIndex) {
            Tab(
                selected = selectedIndex == 0,
                modifier = Modifier.testTag(TestTag.TabDefault),
                onClick = { onSelectedScreen(0) },
                text = { Text(MR.strings.textDefault.stable) }
            )
            Tab(
                selected = selectedIndex == 1,
                modifier = Modifier.testTag(TestTag.TabCustom),
                onClick = { onSelectedScreen(1) },
                text = { Text(MR.strings.textCustom.stable) }
            )
        }

    }

}