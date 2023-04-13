package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.content.WakeWordConfigurationScreens
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.PorcupineViewState

/**
 *  screen for porcupine keyword option
 *  page with default options
 *  page with custom options
 *  bottom bar to switch between pages
 */
@Composable
fun PorcupineKeywordScreen(
    viewState: PorcupineViewState,
    onAction: (PorcupineUiAction) -> Unit
) {

    val navController = rememberNavController()
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()


    Surface(tonalElevation = 3.dp) {
        Scaffold(
            modifier = Modifier
                .testTag(TestTag.PorcupineKeywordScreen)
                .fillMaxSize(),
            topBar = {
                Column {
                    AppBar()
                    val navigation = LocalNavController.current
                    //opens page for porcupine language selection
                    ListElement(
                        modifier = Modifier
                            .testTag(TestTag.PorcupineLanguage)
                            .clickable { navigation.navigate(WakeWordConfigurationScreens.PorcupineLanguage.route) },
                        text = { Text(MR.strings.language.stable) },
                        secondaryText = { Text(viewState.porcupineLanguage.text) }
                    )
                }
            },
            bottomBar = {
                CompositionLocalProvider(
                    LocalNavController provides navController
                ) {
                    Surface(tonalElevation = 3.dp) {
                        //bottom tab bar with pages tabs
                        BottomTabBar(
                            state = pagerState,
                            onSelectIndex = { index ->
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            })
                    }
                }
            }

        ) { paddingValues ->
            //horizontal pager to slide between pages
            Surface(modifier = Modifier.padding(paddingValues)) {
                HorizontalPager(pageCount = 2, state = pagerState) { page ->
                    if (page == 0) {
                        PorcupineKeywordDefaultScreen(
                            viewState = viewState,
                            onAction = onAction
                        )
                    } else {
                        PorcupineKeywordCustomScreen(
                            viewState = viewState,
                            onAction = onAction
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
@Composable
private fun AppBar() {

    val navigation = LocalNavController.current

    TopAppBar(
        title = { Text(MR.strings.porcupineKeyword.stable) },
        navigationIcon = {
            IconButton(
                onClick = navigation::popBackStack,
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
private fun BottomTabBar(state: PagerState, onSelectIndex: (index: Int) -> Unit) {

    Column {

        //tab bar row
        TabRow(selectedTabIndex = state.currentPage) {
            Tab(
                selected = state.currentPage == 0,
                modifier = Modifier.testTag(TestTag.TabDefault),
                onClick = { onSelectIndex(0) },
                text = { Text(MR.strings.textDefault.stable) }
            )
            Tab(
                selected = state.currentPage == 1,
                modifier = Modifier.testTag(TestTag.TabCustom),
                onClick = { onSelectIndex(1) },
                text = { Text(MR.strings.textCustom.stable) }
            )
        }

    }

}