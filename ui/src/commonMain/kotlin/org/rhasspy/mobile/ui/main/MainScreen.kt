package org.rhasspy.mobile.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.resources.icons.RhasspyLogo
import org.rhasspy.mobile.ui.*
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.theme.AppTheme
import org.rhasspy.mobile.viewmodel.ViewModelFactory
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.*
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewState

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
                                    CrashlyticsDialog(
                                        onResult = {
                                            viewModel.onEvent(
                                                CrashlyticsDialogResult(it)
                                            )
                                        }
                                    )
                                }

                                if (viewState.isChangelogDialogVisible) {
                                    ChangelogDialog(
                                        changelog = viewState.changelog,
                                        onDismissRequest = { viewModel.onEvent(CloseChangelog) }
                                    )
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


/**
 * Displays changelog as text in a dialog
 */
@Composable
private fun ChangelogDialog(
    changelog: ImmutableList<String>,
    onDismissRequest: () -> Unit
) {

    Dialog(
        testTag = TestTag.DialogChangelog,
        title = "${translate(MR.strings.changelog.stable)} - ${BuildKonfig.versionName}",
        supportingText = {
            LazyColumn {
                items(changelog) { item ->
                    Text(text = item)
                }
            }
        },
        confirmLabel = MR.strings.close.stable,
        onConfirm = onDismissRequest,
        onDismiss = onDismissRequest
    )

}

@Composable
private fun MainScreenContent(
    screen: NavigationDestination,
    viewState: MainScreenViewState,
    onEvent: (event: MainScreenUiEvent) -> Unit
) {
    NavigationContent(screen) {
        BottomNavigation(
            isShowLogEnabled = viewState.isShowLogEnabled,
            activeIndex = viewState.bottomNavigationIndex,
            onEvent = onEvent
        )
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
            icon = { Icon(Icons.Filled.Mic, MR.strings.home.stable) },
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
            icon = { Icon(Icons.Filled.Timeline, MR.strings.home.stable) },
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
            icon = { Icon(Icons.Filled.Settings, MR.strings.settings.stable) },
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
                icon = { Icon(Icons.Filled.Code, MR.strings.log.stable) },
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