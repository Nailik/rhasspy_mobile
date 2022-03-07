package org.rhasspy.mobile.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.viewModels.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        this.setContent {
            /*   val systemUiController = rememberSystemUiController()
               val useDarkIcons = MaterialTheme.

               SideEffect {
                   systemUiController.setNavigationBarColor(
                       darkIcons = useDarkIcons
                   )
               }*/
            Content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Content(viewModel: MainViewModel = viewModel()) {
    androidx.compose.material.MaterialTheme(
        colors = Colors(
            primary = MaterialTheme.colorScheme.primary,
            primaryVariant = MaterialTheme.colorScheme.primary,
            secondary = MaterialTheme.colorScheme.secondary,
            secondaryVariant = MaterialTheme.colorScheme.secondary,
            background = MaterialTheme.colorScheme.background,
            surface = MaterialTheme.colorScheme.surface,
            error = MaterialTheme.colorScheme.error,
            onPrimary = MaterialTheme.colorScheme.onPrimary,
            onSecondary = MaterialTheme.colorScheme.onSecondary,
            onBackground = MaterialTheme.colorScheme.onBackground,
            onSurface = MaterialTheme.colorScheme.onSurface,
            onError = MaterialTheme.colorScheme.onError,
            isLight = false
        )
    ) {

        MaterialTheme {

            ProvideWindowInsets {

                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {

                    var isBottomNavigationHidden by remember { mutableStateOf(false) }

                    isBottomNavigationHidden = this.maxHeight < 250.dp

                    val navController = rememberNavController()
                    Scaffold(
                        topBar = { TopAppBar() },
                        bottomBar = {
                            //hide bottom navigation with keyboard and small screens
                            if (!isBottomNavigationHidden) {
                                BottomNavigation(navController)
                            }
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
    SmallTopAppBar(
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