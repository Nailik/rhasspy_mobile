package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.viewModels.configuration.WakeWordConfigurationViewModel

/**
 *  screen for porcupine keyword option
 *  page with default options
 *  page with custom options
 *  bottom bar to switch between pages
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun PorcupineKeywordScreen(viewModel: WakeWordConfigurationViewModel) {

    val navController = rememberNavController()
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar() },
        bottomBar = {
            CompositionLocalProvider(
                LocalNavController provides navController
            ) {
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

    ) { paddingValues ->
        //horizontal pager to slide between pages
        Surface(Modifier.padding(paddingValues)) {
            HorizontalPager(count = 2, state = pagerState) { page ->
                if (page == 0) {
                    PorcupineKeywordDefaultScreen(viewModel)
                } else {
                    PorcupineKeywordCustomScreen(viewModel)
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
private fun AppBar() {

    val navigation = LocalNavController.current

    TopAppBar(
        title = {
            Text(MR.strings.wakeWord)
        },
        navigationIcon = {
            IconButton(
                onClick = navigation::popBackStack,
                modifier = Modifier.testTag(TestTag.AppBarBackButton)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back,
                )
            }
        }
    )

}


/**
 * Displays tabs on bottom (default/ custom)
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun BottomTabBar(state: PagerState, onSelectIndex: (index: Int) -> Unit) {

    Column {

        //tab bar row
        TabRow(selectedTabIndex = state.currentPage) {
            Tab(
                selected = state.currentPage == 0,
                onClick = { onSelectIndex(0) },
                text = { Text(MR.strings.textDefault) }
            )
            Tab(
                selected = state.currentPage == 1,
                onClick = { onSelectIndex(1) },
                text = { Text(MR.strings.textCustom) }
            )
        }

    }

}