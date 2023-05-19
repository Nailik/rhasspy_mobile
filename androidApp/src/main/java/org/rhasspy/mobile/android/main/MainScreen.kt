package org.rhasspy.mobile.android.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.configuration.ConfigurationScreen
import org.rhasspy.mobile.android.settings.SettingsScreen
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.icons.RhasspyLogo
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.AppTheme
import org.rhasspy.mobile.viewmodel.ViewModelFactory
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.*
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewState
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

val LocalViewModelFactory = compositionLocalOf<ViewModelFactory> {
    error("No LocalViewModelFactory provided")
}


@Composable
fun MainScreen(viewModelFactory: ViewModelFactory) {

    AppTheme {
        //fixes bright flashing when navigating between screens
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {

            val snackBarHostState = remember { SnackbarHostState() }

            CompositionLocalProvider(
                LocalSnackbarHostState provides snackBarHostState,
                LocalViewModelFactory provides viewModelFactory
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackBarHostState) },
                ) { paddingValues ->


                    var shouldShowCrashlyticsDialog by rememberSaveable { mutableStateOf(MainActivity.isFirstLaunch) }

                    if (shouldShowCrashlyticsDialog && !isDebug()) {
                        CrashlyticsDialog {
                            shouldShowCrashlyticsDialog = false
                        }
                    }

                    Box(modifier = Modifier.padding(paddingValues)) {

                        val viewModel: MainScreenViewModel = LocalViewModelFactory.current.getViewModel()

                        Screen(viewModel) {
                            val screen by viewModel.screen.collectAsState()
                            val viewState by viewModel.viewState.collectAsState()

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

}

@Composable
private fun MainScreenContent(
    screen: MainScreenNavigationDestination,
    viewState: MainScreenViewState,
    onEvent: (event: MainScreenUiEvent) -> Unit
) {

    Column {
        Box(modifier = Modifier.weight(1f)) {
            when (screen) {
                HomeScreen -> HomeScreen()
                ConfigurationScreen -> ConfigurationScreen()
                SettingsScreen -> SettingsScreen()
                LogScreen -> LogScreen()
            }
        }


        AnimatedVisibility(visible = viewState.isBottomNavigationVisible) {
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
fun CrashlyticsDialog(onClose: () -> Unit) {
    val viewModel: LogSettingsViewModel = LocalViewModelFactory.current.getViewModel()
    Dialog(
        modifier = Modifier.testTag(TestTag.DialogCrashlytics),
        onDismissRequest = { /*do not dismiss on outside click*/ },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.onEvent(LogSettingsUiEvent.Change.SetCrashlyticsEnabled(true))
                    onClose()
                },
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.confirm.stable)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.onEvent(LogSettingsUiEvent.Change.SetCrashlyticsEnabled(false))
                    onClose()
                },
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.deny.stable)
            }
        },
        headline = {
            Text(MR.strings.crashlytics.stable)
        },
        supportingText = {
            Text(MR.strings.crashlyticsDialogText.stable)
        }
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
            selected = activeIndex == 1,
            onClick = { onEvent(Navigate(ConfigurationScreen)) }
        )

        NavigationBarItem(
            modifier = Modifier.testTag(SettingsScreen),
            icon = {
                Icon(
                    if (activeIndex == 2) {
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
            selected = activeIndex == 2,
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
                selected = activeIndex == 3,
                onClick = { onEvent(Navigate(LogScreen)) }
            )
        }

    }

}