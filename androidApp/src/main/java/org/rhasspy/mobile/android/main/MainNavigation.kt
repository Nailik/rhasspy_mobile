package org.rhasspy.mobile.android.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.configuration.addConfigurationScreens
import org.rhasspy.mobile.android.settings.addSettingsScreen
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.ui.theme.AppTheme
import org.rhasspy.mobile.viewmodel.ViewModelFactory
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Change.SetCrashlyticsEnabled
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsViewModel

/**
 * main screens, full size
 */
enum class MainScreens(val route: String) {
    BoomBarScreen("MainScreens_BoomBarScreen")
}

/**
 * root layout contains main navigation between the
 * 3 screens in the bottom bar and the about screen
 */
@Composable
fun MainNavigation(viewModelFactory: ViewModelFactory) {
    AppTheme {
        //fixes bright flashing when navigating between screens
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {

            val navController = rememberNavController()
            val snackBarHostState = remember { SnackbarHostState() }

            CompositionLocalProvider(
                LocalMainNavController provides navController,
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

                    NavHost(
                        navController = navController,
                        startDestination = MainScreens.BoomBarScreen.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(MainScreens.BoomBarScreen.route) {
                            BottomBarScreensNavigation()
                        }
                        addConfigurationScreens()
                        addSettingsScreen()
                    }
                }
            }
        }
    }
}

/**
 * dialog if user wants to enable crashlytics
 */
@Composable
private fun CrashlyticsDialog(onClose: () -> Unit) {
    val viewModel: LogSettingsViewModel = LocalViewModelFactory.current.getViewModel()
    AlertDialog(
        modifier = Modifier.testTag(TestTag.DialogCrashlytics),
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.onEvent(SetCrashlyticsEnabled(true))
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
                    viewModel.onEvent(SetCrashlyticsEnabled(false))
                    onClose()
                },
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.deny.stable)
            }
        },
        title = {
            Text(MR.strings.crashlytics.stable)
        },
        text = {
            Text(MR.strings.crashlyticsDialogText.stable)
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}
