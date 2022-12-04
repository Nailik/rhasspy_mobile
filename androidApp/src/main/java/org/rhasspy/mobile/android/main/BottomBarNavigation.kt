package org.rhasspy.mobile.android.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.getViewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.configuration.ConfigurationScreen
import org.rhasspy.mobile.android.settings.SettingsScreen
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.NavigationItem
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

val LocalMainNavController = compositionLocalOf<NavController> {
    error("No NavController provided")
}

val LocalNavController = compositionLocalOf<NavController> {
    error("No NavController provided")
}

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}


/**
 * screens in the bottom bar
 */
enum class BottomBarScreens {
    HomeScreen,
    ConfigurationScreen,
    SettingsScreen,
    LogScreen
}

@Composable
fun BottomBarScreensNavigation(viewModel: HomeScreenViewModel = getViewModel()) {

    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = { BottomNavigation(viewModel, navController) }
        ) { paddingValues ->

            NavHost(
                navController = navController,
                startDestination = BottomBarScreens.HomeScreen.name,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(BottomBarScreens.HomeScreen.name) {
                    HomeScreen()
                }
                composable(BottomBarScreens.ConfigurationScreen.name) {
                    ConfigurationScreen()
                }
                composable(BottomBarScreens.SettingsScreen.name) {
                    SettingsScreen()
                }
                composable(BottomBarScreens.LogScreen.name) {
                    LogScreen()
                }
            }
        }
    }
}

@Composable
fun BottomNavigation(viewModel: HomeScreenViewModel, navController: NavController) {

    NavigationBar {

        val currentBackStackEntry by navController.currentBackStackEntryAsState()

        NavigationItem(screen = BottomBarScreens.HomeScreen,
            icon = {
                Icon(
                    if (currentBackStackEntry?.destination?.route == BottomBarScreens.HomeScreen.name) {
                        Icons.Filled.Mic
                    } else {
                        Icons.Outlined.Mic
                    }, MR.strings.home
                )
            },
            label = { Text(MR.strings.home) })

        NavigationItem(screen = BottomBarScreens.ConfigurationScreen,
            icon = { Icon(painterResource(MR.images.ic_launcher.drawableResId), MR.strings.configuration, Modifier.size(24.dp)) },
            label = { Text(MR.strings.configuration) })

        NavigationItem(screen = BottomBarScreens.SettingsScreen,
            icon = {
                Icon(
                    if (currentBackStackEntry?.destination?.route == BottomBarScreens.SettingsScreen.name) {
                        Icons.Filled.Settings
                    } else {
                        Icons.Outlined.Settings
                    }, MR.strings.settings
                )
            },
            label = { Text(MR.strings.settings) })

        if (viewModel.isShowLogEnabled.collectAsState().value) {
            NavigationItem(screen = BottomBarScreens.LogScreen,
                icon = { Icon(Icons.Filled.Code, MR.strings.log) },
                label = { Text(MR.strings.log) })
        }

    }

}