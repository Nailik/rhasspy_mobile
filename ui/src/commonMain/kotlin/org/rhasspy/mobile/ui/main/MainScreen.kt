package org.rhasspy.mobile.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.main.SettingsScreen
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.resources.icons.RhasspyLogo
import org.rhasspy.mobile.ui.LocalSnackBarHostState
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.AppTheme
import org.rhasspy.mobile.viewmodel.ViewModelFactory
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.ConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.DialogScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.HomeScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.LogScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.SettingsScreen
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.CrashlyticsDialogResult
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModelFactory: ViewModelFactory) {

    Box(modifier = Modifier.fillMaxSize()) {
        AppTheme {
            //fixes bright flashing when navigating between screens
            Surface(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {

                val snackBarHostState = remember { SnackbarHostState() }

                CompositionLocalProvider(
                    LocalSnackBarHostState provides snackBarHostState,
                    LocalViewModelFactory provides viewModelFactory
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackBarHostState) },
                    ) { paddingValues ->


                        Box(modifier = Modifier.padding(paddingValues)) {

                            val viewModel: MainScreenViewModel =
                                LocalViewModelFactory.current.getViewModel()
                            Screen(screenViewModel = viewModel) {
                                val screen by viewModel.screen.collectAsState()
                                val viewState by viewModel.viewState.collectAsState()

                                if (viewState.isShowCrashlyticsDialog) {
                                    CrashlyticsDialog(onResult = {
                                        viewModel.onEvent(
                                            CrashlyticsDialogResult(it)
                                        )
                                    })
                                }

                                MainScreenContent(
                                    screen = screen,
                                    viewState = viewState,
                                    onEvent = viewModel::onEvent
                                )
                            }
                        }

                    }
                }
            }
        }

        if (isDebug()) {
            Text(
                text = "DEBUG",
                modifier = Modifier
                    .rotate(45F)
                    .offset(50.dp)
                    .background(Color.Red)
                    .width(180.dp)
                    .align(Alignment.TopEnd),
                textAlign = TextAlign.Center
            )
        }
    }

}

val CONTENT_ANIMATION_DURATION = 100

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MainScreenContent(
    screen: MainScreenNavigationDestination,
    viewState: MainScreenViewState,
    onEvent: (event: MainScreenUiEvent) -> Unit
) {

    Column {
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(targetState = screen,
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
                    HomeScreen          -> HomeScreen()
                    DialogScreen        -> DialogScreen()
                    ConfigurationScreen -> ConfigurationScreen()
                    SettingsScreen      -> SettingsScreen()
                    LogScreen           -> LogScreen()
                }
            }
        }


        if (viewState.isBottomNavigationVisible) {
            BottomNavigation(
                isShowLogEnabled = viewState.isShowLogEnabled,
                activeIndex = viewState.bottomNavigationIndex,
                onEvent = onEvent
            )
        }

    }

}


/**
 * dialog if user wants to enable crashlytics
 */
@Composable
fun CrashlyticsDialog(onResult: (result: Boolean) -> Unit) {
    Dialog(
        testTag = TestTag.DialogCrashlytics,
        icon = Icons.Filled.BugReport,
        title = MR.strings.crashlytics.stable,
        message = MR.strings.crashlyticsDialogText.stable,
        confirmLabel = MR.strings.confirm.stable,
        dismissLabel = MR.strings.deny.stable,
        onConfirm = { onResult(true) },
        onDismiss = { onResult(false) },
        onClose = {}
    )
}


/**
 * navigation bar on bottom
 */
@Composable
private fun BottomNavigation(
    isShowLogEnabled: Boolean,
    activeIndex: Int,
    onEvent: (event: MainScreenUiEvent) -> Unit
) {

    NavigationBar {

        NavigationBarItem(
            modifier = Modifier.testTag(HomeScreen),
            icon = {
                Icon(
                    if (activeIndex == 0) {
                        Icons.Filled.Mic
                    } else {
                        Icons.Outlined.Mic
                    },
                    MR.strings.home.stable
                )
            },
            label = {
                Text(
                    resource = MR.strings.home.stable,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            selected = activeIndex == 0,
            onClick = { onEvent(Navigate(HomeScreen)) }
        )

        NavigationBarItem(
            modifier = Modifier.testTag(DialogScreen),
            icon = {
                Icon(
                    if (activeIndex == 0) {
                        Icons.Filled.Timeline
                    } else {
                        Icons.Outlined.Timeline
                    },
                    MR.strings.home.stable
                )
            },
            label = {
                Text(
                    resource = MR.strings.dialog.stable,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            selected = activeIndex == 1,
            onClick = { onEvent(Navigate(DialogScreen)) }
        )

        NavigationBarItem(
            modifier = Modifier.testTag(ConfigurationScreen),
            icon = {
                Icon(
                    RhasspyLogo,
                    MR.strings.configuration.stable,
                    Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    resource = MR.strings.configuration.stable,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            selected = activeIndex == 2,
            onClick = { onEvent(Navigate(ConfigurationScreen)) }
        )

        NavigationBarItem(
            modifier = Modifier.testTag(SettingsScreen),
            icon = {
                Icon(
                    if (activeIndex == 3) {
                        Icons.Filled.Settings
                    } else {
                        Icons.Outlined.Settings
                    },
                    MR.strings.settings.stable
                )
            },
            label = {
                Text(
                    resource = MR.strings.settings.stable,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            selected = activeIndex == 3,
            onClick = { onEvent(Navigate(SettingsScreen)) }
        )

        if (isShowLogEnabled) {
            NavigationBarItem(
                modifier = Modifier.testTag(LogScreen),
                icon = {
                    Icon(Icons.Filled.Code, MR.strings.log.stable)
                },
                label = {
                    Text(
                        resource = MR.strings.log.stable,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                selected = activeIndex == 4,
                onClick = { onEvent(Navigate(LogScreen)) }
            )
        }

    }

}