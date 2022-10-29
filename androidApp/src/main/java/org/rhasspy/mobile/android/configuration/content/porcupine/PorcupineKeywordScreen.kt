package org.rhasspy.mobile.android.configuration.content.porcupine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword
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
                    viewModel = viewModel,
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
                    DefaultKeywords(viewModel)
                } else {
                    CustomKeywords(viewModel)
                }
            }
        }
    }
}

/**
 * default keywords screen
 */
@Composable
private fun DefaultKeywords(viewModel: WakeWordConfigurationViewModel) {

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
                        viewModel.clickPorcupineKeywordDefault(index)
                    },
                    icon = {
                        Checkbox(
                            checked = element.enabled,
                            onCheckedChange = { enabled ->
                                viewModel.togglePorcupineKeywordDefault(index, enabled)
                            })
                    },
                    text = {
                        Text(element.option.text)
                    },
                )

                if (element.enabled) {
                    //sensitivity of porcupine
                    SliderListItem(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        text = MR.strings.sensitivity,
                        value = element.sensitivity,
                        onValueChange = { sensitivity -> viewModel.updateWakeWordPorcupineKeywordDefaultSensitivity(index, sensitivity) }
                    )
                }

                CustomDivider()
            }
        )
    }

}

/**
 * Custom keywords screen
 */
@Composable
private fun CustomKeywords(viewModel: WakeWordConfigurationViewModel) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        val options by viewModel.wakeWordPorcupineKeywordCustomOptions.collectAsState()
        options.forEachIndexed { index, element ->

            CustomKeywordListItem(
                element = element,
                onClick = {
                    viewModel.clickPorcupineKeywordCustom(index)
                },
                onToggle = { enabled ->
                    viewModel.togglePorcupineKeywordCustom(index, enabled)
                },
                onDelete = {
                    viewModel.deletePorcupineKeywordCustom(index)
                },
                onUpdateSensitivity = { sensitivity ->
                    viewModel.updateWakeWordPorcupineKeywordCustomSensitivity(index, sensitivity)
                }
            )

            CustomDivider()

        }

        val optionsRemoved by viewModel.wakeWordPorcupineKeywordCustomOptionsRemoved.collectAsState()
        optionsRemoved.forEachIndexed { index, element ->

            CustomKeywordDeletedListItem(
                element = element,
                onUndo = { viewModel.undoWakeWordPorcupineCustomKeywordDeleted(index) }
            )

            CustomDivider()

        }

    }

}

@Composable
private fun CustomKeywordListItem(
    element: PorcupineCustomKeyword,
    onClick: () -> Unit,
    onToggle: (enabled: Boolean) -> Unit,
    onDelete: () -> Unit,
    onUpdateSensitivity: (sensitivity: Float) -> Unit,
) {
    ListElement(
        modifier = Modifier.clickable(onClick = onClick),
        icon = {
            Checkbox(
                checked = element.enabled,
                onCheckedChange = onToggle
            )
        },
        text = {
            Text(element.fileName)
        },
        trailing = {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = MR.strings.defaultText
                )
            }
        }
    )

    if (element.enabled) {
        //sensitivity of porcupine
        SliderListItem(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = MR.strings.sensitivity,
            value = element.sensitivity,
            onValueChange = onUpdateSensitivity
        )
    }
}

@Composable
private fun CustomKeywordDeletedListItem(
    element: PorcupineCustomKeyword,
    onUndo: () -> Unit,
) {
    ListElement(
        text = {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = element.fileName
            )
        },
        trailing = {
            IconButton(onClick = onUndo) {
                Icon(
                    imageVector = Icons.Filled.Block,
                    contentDescription = MR.strings.defaultText
                )
            }
        }
    )
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
 * custom keywords action buttons (download and open file)
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CustomKeywordsActionButtons(state: PagerState, viewModel: WakeWordConfigurationViewModel) {
    if (state.currentPage == 1) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            FilledTonalButton(
                onClick = viewModel::downloadCustomPorcupineKeyword,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = MR.strings.fileOpen
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(MR.strings.download)
                })

            FilledTonalButton(
                onClick = viewModel::addCustomPorcupineKeyword,
                content = {
                    Icon(
                        imageVector = Icons.Filled.FileOpen,
                        contentDescription = MR.strings.fileOpen
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(MR.strings.fileOpen)
                })

        }
    }
}

/**
 * Displays tabs on bottom (default/ custom)
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun BottomTabBar(state: PagerState, viewModel: WakeWordConfigurationViewModel, onSelectIndex: (index: Int) -> Unit) {

    Column {
        //action buttons
        CustomKeywordsActionButtons(state, viewModel)

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