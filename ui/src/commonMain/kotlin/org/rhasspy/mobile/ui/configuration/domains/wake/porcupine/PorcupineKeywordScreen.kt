package org.rhasspy.mobile.ui.configuration.domains.wake.porcupine

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.ScreenContent
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.TonalElevationLevel2
import org.rhasspy.mobile.ui.theme.horizontalAnimationSpec
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationUiEvent.PorcupineUiEvent.Action.PageClick
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationUiEvent.PorcupineUiEvent.Action.PorcupineLanguageClick
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationViewModel

/**
 *  screen for porcupine keyword option
 *  page with default options
 *  page with custom options
 *  bottom bar to switch between pages
 */
@Composable
fun PorcupineKeywordScreen(viewModel: WakeDomainConfigurationViewModel) {

    val viewState by viewModel.viewState.collectAsState()
    val editData = viewState.editData.wakeWordPorcupineConfigurationData

    ScreenContent(
        title = MR.strings.porcupineKeyword.stable,
        viewModel = viewModel,
        tonalElevation = TonalElevationLevel2,
    ) {

        Surface(tonalElevation = 3.dp) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = {
                    //opens page for porcupine language selection
                    ListElement(
                        modifier = Modifier
                            .testTag(TestTag.PorcupineLanguage)
                            .clickable { viewModel.onEvent(PorcupineLanguageClick) },
                        text = { Text(MR.strings.language.stable) },
                        secondaryText = { Text(editData.porcupineLanguage.text) }
                    )
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
                Surface(
                    modifier = Modifier
                        .padding(paddingValues)
                        .testTag(TestTag.PorcupineKeywordScreen)
                ) {

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