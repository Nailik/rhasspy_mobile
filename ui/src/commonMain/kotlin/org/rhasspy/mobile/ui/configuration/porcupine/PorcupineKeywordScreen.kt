package org.rhasspy.mobile.ui.configuration.porcupine

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TopAppBar
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
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.PageClick
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.PorcupineLanguageClick
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordPorcupineConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination.CustomKeywordScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination.DefaultKeywordScreen

/**
 *  screen for porcupine keyword option
 *  page with default options
 *  page with custom options
 *  bottom bar to switch between pages
 */
@Composable
fun PorcupineKeywordScreen(
    porcupineScreen: PorcupineKeywordConfigurationScreenDestination,
    editData: WakeWordPorcupineConfigurationData,
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
                        secondaryText = { Text(editData.porcupineLanguage.text) }
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

                AnimatedContent(
                    targetState = porcupineScreen,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            slideInHorizontally(
                                animationSpec = tween(CONTENT_ANIMATION_DURATION),
                                initialOffsetX = { fullWidth -> fullWidth }
                            ) togetherWith slideOutHorizontally(
                                animationSpec = tween(CONTENT_ANIMATION_DURATION),
                                targetOffsetX = { fullWidth -> -fullWidth })
                        } else {
                            slideInHorizontally(
                                animationSpec = tween(CONTENT_ANIMATION_DURATION),
                                initialOffsetX = { fullWidth -> -fullWidth }
                            ) togetherWith slideOutHorizontally(
                                animationSpec = tween(CONTENT_ANIMATION_DURATION),
                                targetOffsetX = { fullWidth -> fullWidth })
                        }
                    }) { targetState ->
                    when (targetState) {
                        DefaultKeywordScreen -> PorcupineKeywordDefaultScreen(
                            editData = editData,
                            onEvent = onEvent
                        )

                        CustomKeywordScreen  -> PorcupineKeywordCustomScreen(
                            editData = editData,
                            onEvent = onEvent
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