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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.configuration.ConfigurationScreen
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.item.NavigationItem
import org.rhasspy.mobile.android.navigation.BottomBarScreenType
import org.rhasspy.mobile.android.navigation.NavigationParams
import org.rhasspy.mobile.android.settings.SettingsScreen
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.icons.RhasspyLogo
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewModel

val LocalConfigurationNavController = compositionLocalOf<NavController> {
    error("No NavController provided")
}

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
 * navigation holder for bottom navigation bar screens
 */
@Composable
fun BottomBarScreensNavigation(viewModel: HomeScreenViewModel = get()) {

    val navController = rememberNavController()

    val snackBarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalSnackbarHostState provides snackBarHostState
    ) {
        val viewState by viewModel.viewState.collectAsState()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackBarHostState) },
            bottomBar = {
                BottomNavigation(
                    isShowLogEnabled = viewState.isShowLogEnabled,
                    navController = navController
                )
            }
        ) { paddingValues ->

            NavHost(
                navController = navController,
                startDestination = BottomBarScreenType.HomeScreen.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(BottomBarScreenType.HomeScreen.route) {
                    HomeScreen()
                }
                composable(
                    BottomBarScreenType.ConfigurationScreen
                        .appendOptionalParameter(
                            NavigationParams.ScrollToError,
                            "{${NavigationParams.ScrollToError.paramName}}"
                        ),
                    arguments = listOf(navArgument(NavigationParams.ScrollToError.paramName) {
                        defaultValue = false
                    })
                ) {
                    //TODO scroll to error
                    ConfigurationScreen()
                }
                composable(BottomBarScreenType.SettingsScreen.route) {
                    SettingsScreen()
                }
                composable(BottomBarScreenType.LogScreen.route) {
                    LogScreen()
                }
            }
        }
    }
}

/**
 * navigation bar on bottom
 */
@Composable
fun BottomNavigation(
    isShowLogEnabled: StateFlow<Boolean>,
    navController: NavController) {

    NavigationBar {

        val currentBackStackEntry by navController.currentBackStackEntryAsState()

        NavigationItem(
            screen = BottomBarScreenType.HomeScreen,
            icon = {
                Icon(
                    if (currentBackStackEntry?.destination?.route == BottomBarScreenType.HomeScreen.route) {
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
            }
        )

        NavigationItem(
            screen = BottomBarScreenType.ConfigurationScreen,
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
            }
        )

        NavigationItem(
            screen = BottomBarScreenType.SettingsScreen,
            icon = {
                Icon(
                    if (currentBackStackEntry?.destination?.route == BottomBarScreenType.SettingsScreen.route) {
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
            }
        )

        if (isShowLogEnabled.collectAsState().value) {
            NavigationItem(screen = BottomBarScreenType.LogScreen,
                icon = { Icon(Icons.Filled.Code, MR.strings.log.stable) },
                label = { Text(MR.strings.log.stable) }
            )
        }

    }

}