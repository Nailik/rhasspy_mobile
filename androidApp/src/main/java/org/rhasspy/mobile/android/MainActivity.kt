package org.rhasspy.mobile.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.viewModels.GlobalData
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

    val systemUiController = rememberSystemUiController()

    val themeOption = AppSettings.themeOption.observe()

    val darkTheme = (isSystemInDarkTheme() && themeOption == ThemeOptions.System) || themeOption == ThemeOptions.Dark
    val colorScheme = if (darkTheme) DarkThemeColors else LightThemeColors

    systemUiController.setSystemBarsColor(colorScheme.background, darkIcons = !darkTheme)
    systemUiController.setNavigationBarColor(colorScheme.background, darkIcons = !darkTheme)
    systemUiController.setStatusBarColor(colorScheme.background, darkIcons = !darkTheme)

    androidx.compose.material.MaterialTheme(
        colors = colorScheme.toColors(isLight = !darkTheme)
    ) {

        MaterialTheme(colorScheme = colorScheme) {

            ProvideWindowInsets {

                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {

                    var isBottomNavigationHidden by remember { mutableStateOf(false) }

                    isBottomNavigationHidden = this.maxHeight < 250.dp

                    val navController = rememberNavController()
                    Scaffold(
                        topBar = { TopAppBar(viewModel) },
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
fun TopAppBar(viewModel: MainViewModel) {
    SmallTopAppBar(
        title = { Text(MR.strings.appName) },
        actions = {
            AnimatedVisibility(
                enter = fadeIn(animationSpec = tween(50)),
                exit = fadeOut(animationSpec = tween(50)),
                visible = GlobalData.unsavedChanges.observe()
            ) {
                Row(modifier = Modifier.padding(end = 16.dp)) {
                    IconButton(onClick = { viewModel.resetChanges() })
                    {
                        Icon(
                            imageVector = Icons.Filled.Restore,
                            contentDescription = "ewr"
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { viewModel.saveAndApplyChanges() }) {
                        Icon(
                            imageVector = Icons.Filled.PublishedWithChanges,
                            contentDescription = "ewr"
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun BottomNavigation(navController: NavHostController) {
    NavigationBar {

        val array = mutableListOf(Screens.HomeScreen, Screens.ConfigurationScreen, Screens.SettingsScreen)

        if (AppSettings.isShowLog.value.observe()) {
            array.add(Screens.LogScreen)
        }

        array.forEach { screen ->
            NavigationItem(screen, navController)
        }
    }
}

@Composable
fun RowScope.NavigationItem(screen: Screens, navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
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