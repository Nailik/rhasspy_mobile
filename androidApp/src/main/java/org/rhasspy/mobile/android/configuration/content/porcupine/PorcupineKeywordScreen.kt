package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
 *  list of porcupine keyword option, contains option to add item from file manager
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
        },
        floatingActionButton = {
            //button to select custom file
            if (pagerState.currentPage == 1) {
                FloatingActionButton(onClick = viewModel::selectPorcupineWakeWordFile) {
                    Icon(imageVector = Icons.Filled.FileOpen, contentDescription = MR.strings.fileOpen)
                }
            }
        }

    ) { paddingValues ->
        //horizontal pager to slide between pages
        Surface(Modifier.padding(paddingValues)) {
            HorizontalPager(count = 2, state = pagerState) { page ->
                if (page == 0) {
                    PredefinedKeywords(viewModel)
                } else {
                    CustomKeywords(viewModel)
                }
            }
        }
    }
}

/**
 * predefined keywords
 */
@Composable
private fun PredefinedKeywords(viewModel: WakeWordConfigurationViewModel) {

    val options by viewModel.wakeWordPorcupineKeywordDefaultOptions.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        items(
            count = options.size,
            itemContent = { index ->

                val element = options.elementAt(index)

                ListElement(
                    modifier = Modifier.clickable {
                        viewModel.clickPredefinedPorcupineKeyword(index)
                    },
                    icon = {
                        Checkbox(
                            checked = element.second,
                            onCheckedChange = { enabled ->
                                viewModel.togglePredefinedPorcupineKeyword(index, enabled)
                            })
                    },
                    text = {
                        Text(element.first.text)
                    },
                )

                if (element.second) {
                    //sensitivity of porcupine
                    SliderListItem(
                        mdoifier = Modifier.padding(horizontal = 12.dp),
                        text = MR.strings.sensitivity,
                        value = element.third,
                        onValueChange = { sensitivity -> viewModel.updateWakeWordPorcupineSensitivity(index, sensitivity) }
                    )
                }

                CustomDivider()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomKeywords(viewModel: WakeWordConfigurationViewModel) {

    Box(modifier = Modifier.fillMaxSize()) {

    }

}

/**
 * app bar for the keyword
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