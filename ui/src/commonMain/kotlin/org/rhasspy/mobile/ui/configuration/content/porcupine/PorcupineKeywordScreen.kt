package org.rhasspy.mobile.ui.configuration.content.porcupine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationViewState.PorcupineViewState
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination.CustomKeywordScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination.DefaultKeywordScreen

/**
 *  screen for porcupine keyword option
 *  page with default options
 *  page with custom options
 *  bottom bar to switch between pages
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PorcupineKeywordScreen(
    porcupineScreen: PorcupineKeywordConfigurationScreenDestination,
    viewState: PorcupineViewState,
    onEvent: (PorcupineUiEvent) -> Unit
) {
    Surface(tonalElevation = 3.dp) {
        Scaffold(
            modifier = Modifier
                .testTag(TestTag.PorcupineKeywordScreen)
                .fillMaxSize(),
            topBar = {
                Column {
                    AppBar(onEvent)
                    //opens page for porcupine language selection
                    ListElement(
                        modifier = Modifier
                            .testTag(TestTag.PorcupineLanguage)
                            .clickable { onEvent(PorcupineLanguageClick) },
                        text = { Text(MR.strings.language.stable) },
                        secondaryText = { Text(viewState.porcupineLanguage.text) }
                    )
                }
            },
            bottomBar = {
                Surface(tonalElevation = 3.dp) {
                    //bottom tab bar with pages tabs
                    BottomTabBar(
                        selectedIndex = porcupineScreen.index,
                        onSelectedScreen = { onEvent(PageClick(it)) }
                    )
                }
            }

        ) { paddingValues ->
            //horizontal pager to slide between pages
            Surface(modifier = Modifier.padding(paddingValues)) {

                when (porcupineScreen) {
                    DefaultKeywordScreen -> PorcupineKeywordDefaultScreen(
                        viewState = viewState,
                        onEvent = onEvent
                    )

                    CustomKeywordScreen -> PorcupineKeywordCustomScreen(
                        viewState = viewState,
                        onEvent = onEvent
                    )
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
    onSelectedScreen: (screen: PorcupineKeywordConfigurationScreenDestination) -> Unit
) {

    Column {

        //tab bar row
        TabRow(selectedTabIndex = selectedIndex) {
            Tab(
                selected = selectedIndex == 0,
                modifier = Modifier.testTag(TestTag.TabDefault),
                onClick = { onSelectedScreen(DefaultKeywordScreen) },
                text = { Text(MR.strings.textDefault.stable) }
            )
            Tab(
                selected = selectedIndex == 1,
                modifier = Modifier.testTag(TestTag.TabCustom),
                onClick = { onSelectedScreen(CustomKeywordScreen) },
                text = { Text(MR.strings.textCustom.stable) }
            )
        }

    }

}