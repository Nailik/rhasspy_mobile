package org.rhasspy.mobile.android.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.rhasspy.mobile.android.configuration.addConfigurationScreens
import org.rhasspy.mobile.android.settings.addSettingsScreen
import org.rhasspy.mobile.android.theme.AppTheme
import org.rhasspy.mobile.android.theme.getIsDarkTheme

/**
 * main screens, full size
 */
enum class MainScreens {
    BoomBarScreen
}

/**
 * root layout contains main navigation between the
 * 3 screens in the bottom bar and the about screen
 */
@Preview
@Composable
fun MainNavigation() {
    AppTheme {
        rememberSystemUiController().setStatusBarColor(MaterialTheme.colorScheme.surfaceVariant, darkIcons = !getIsDarkTheme())
        //fixes bright flashing when navigating between screens
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {


            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            CompositionLocalProvider(
                LocalMainNavController provides navController,
                LocalSnackbarHostState provides snackbarHostState
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { paddingValues ->

                    NavHost(
                        navController = navController,
                        startDestination = MainScreens.BoomBarScreen.name,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(MainScreens.BoomBarScreen.name) {
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



