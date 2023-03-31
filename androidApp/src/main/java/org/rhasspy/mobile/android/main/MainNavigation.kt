package org.rhasspy.mobile.android.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.multidex.BuildConfig
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.addConfigurationScreens
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.settings.addSettingsScreen
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.viewmodel.settings.LogSettingsViewModel

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
@Preview
@Composable
fun MainNavigation() {
    AppTheme {
        //fixes bright flashing when navigating between screens
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {

            val navController = rememberNavController()
            val snackBarHostState = remember { SnackbarHostState() }

            CompositionLocalProvider(
                LocalMainNavController provides navController,
                LocalSnackbarHostState provides snackBarHostState
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackBarHostState) },
                ) { paddingValues ->

                    var shouldShowCrashlyticsDialog by remember { mutableStateOf(MainActivity.isFirstLaunch) }

                    if (shouldShowCrashlyticsDialog && !BuildConfig.DEBUG) {
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
private fun CrashlyticsDialog(viewModel: LogSettingsViewModel = get(), onClose: () -> Unit) {
    AlertDialog(
        modifier = Modifier.testTag(TestTag.DialogCrashlytics),
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.toggleCrashlyticsEnabled(true)
                    onClose()
                },
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.confirm)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.toggleCrashlyticsEnabled(false)
                    onClose()
                },
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.deny)
            }
        },
        title = {
            Text(MR.strings.crashlytics)
        },
        text = {
            Text(MR.strings.crashlyticsDialogText)
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}
