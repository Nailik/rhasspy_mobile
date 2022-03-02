package org.rhasspy.mobile.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import org.rhasspy.mobile.MR

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        val splashWasDisplayed = savedInstanceState != null
        if (!splashWasDisplayed) {
            installSplashScreen()
        }

        @OptIn(ExperimentalMaterial3Api::class)
        this.setContent {
            MaterialTheme {
                ProvideWindowInsets {
                    val navController = rememberNavController()
                    Scaffold(
                        topBar = {
                            TopAppBar()
                        },
                        bottomBar = {
                            BottomNavigation(navController)
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = Screens.HomeScreen.name,
                            modifier = Modifier.padding(
                                paddingValues.calculateLeftPadding(LayoutDirection.Ltr),
                                paddingValues.calculateTopPadding(),
                                paddingValues.calculateRightPadding(LayoutDirection.Ltr),
                                paddingValues.calculateBottomPadding()
                            )
                        ) {
                            composable(Screens.HomeScreen.name) {
                                HomeScreen()
                            }
                            composable(Screens.ConfigurationScreen.name) {
                                ConfigurationScreen()
                            }
                            composable(Screens.SettingsScreen.name) {
                                SettingsScreen()
                            }
                            composable(Screens.LogScreen.name) {
                                LogScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class Screens(val icon: @Composable () -> Unit, val label: @Composable () -> Unit) {
    HomeScreen({ Icon(Icons.Filled.Mic, MR.strings.home) }, { Text(MR.strings.home) }),
    ConfigurationScreen(
        { Icon(painterResource(R.drawable.ic_launcher), MR.strings.configuration, Modifier.size(Dp(24f))) },
        { Text(MR.strings.configuration) }),
    SettingsScreen({ Icon(Icons.Filled.Settings, MR.strings.settings) }, { Text(MR.strings.settings) }),
    LogScreen({ Icon(Icons.Filled.Code, MR.strings.log) }, { Text(MR.strings.log) })
}

@Composable
fun TopAppBar() {
    MediumTopAppBar(
        title = { Text(MR.strings.appName) }
    )
}

@Composable
fun BottomNavigation(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        arrayOf(Screens.HomeScreen, Screens.ConfigurationScreen, Screens.SettingsScreen, Screens.LogScreen).forEach { screen ->
            NavigationBarItem(selected = currentDestination?.hierarchy?.any { it.route == screen.name } == true,
                onClick = {
                    navController.navigate(screen.name) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = screen.icon,
                label = screen.label
            )
        }
    }
}