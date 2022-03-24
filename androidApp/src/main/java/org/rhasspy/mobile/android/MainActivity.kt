package org.rhasspy.mobile.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
import org.rhasspy.mobile.AppActivity
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.permissions.requestMicrophonePermission
import org.rhasspy.mobile.android.permissions.requestOverlayPermission
import org.rhasspy.mobile.android.screens.ConfigurationScreen
import org.rhasspy.mobile.android.screens.HomeScreen
import org.rhasspy.mobile.android.screens.LogScreen
import org.rhasspy.mobile.android.screens.SettingsScreen
import org.rhasspy.mobile.android.theme.DarkThemeColors
import org.rhasspy.mobile.android.theme.LightThemeColors
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.observe
import org.rhasspy.mobile.android.utils.toColors
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.viewModels.GlobalData
import org.rhasspy.mobile.viewModels.HomeScreenViewModel


class MainActivity : AppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        MicrophonePermission.init(this)
        OverlayPermission.init(this)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        this.setContent {
            Content()
        }
    }
}

@Composable
fun AppTheme(systemUiTheme: Boolean, content: @Composable () -> Unit) {

    val themeOption = AppSettings.themeOption.observe()

    val darkTheme = (isSystemInDarkTheme() && themeOption == ThemeOptions.System) || themeOption == ThemeOptions.Dark
    val colorScheme = if (darkTheme) DarkThemeColors else LightThemeColors

    if (systemUiTheme) {
        //may be used inside overlay and then the context is not an activity
        val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(colorScheme.background, darkIcons = !darkTheme)
        systemUiController.setNavigationBarColor(colorScheme.background, darkIcons = !darkTheme)
        systemUiController.setStatusBarColor(colorScheme.background, darkIcons = !darkTheme)
    }

    androidx.compose.material.MaterialTheme(
        colors = colorScheme.toColors(isLight = !darkTheme),
        typography = MaterialTheme.typography.toOldTypography()
    ) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Content(viewModel: HomeScreenViewModel = viewModel()) {

    AppTheme(true) {

        ProvideWindowInsets {

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {

                var isBottomNavigationHidden by remember { mutableStateOf(false) }

                isBottomNavigationHidden = this.maxHeight < 250.dp

                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    topBar = { TopAppBar(viewModel, snackbarHostState, navController) },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            HomeScreen(snackbarHostState)
                        }
                        composable(Screens.ConfigurationScreen.name) {
                            ConfigurationScreen(snackbarHostState)
                        }
                        composable(Screens.SettingsScreen.name) {
                            SettingsScreen(snackbarHostState)
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

private fun Typography.toOldTypography(): androidx.compose.material.Typography {
    return androidx.compose.material.Typography(
        h1 = this.displayLarge,
        h2 = this.displayMedium,
        h3 = this.displaySmall,
        h4 = this.headlineLarge,
        h5 = this.headlineMedium,
        h6 = this.headlineSmall,
        subtitle1 = this.titleLarge,
        subtitle2 = this.titleSmall,
        body1 = this.bodyLarge,
        body2 = this.bodySmall,
        button = this.labelLarge,
        caption = this.labelMedium,
        overline = this.labelSmall,
    )
}

enum class Screens(val icon: @Composable () -> Unit, val label: @Composable () -> Unit) {
    HomeScreen({ Icon(Icons.Filled.Mic, MR.strings.home) }, { Text(MR.strings.home) }),
    ConfigurationScreen(
        { Icon(painterResource(MR.images.ic_launcher.drawableResId), MR.strings.configuration, Modifier.size(24.dp)) },
        { Text(MR.strings.configuration) }),
    SettingsScreen({ Icon(Icons.Filled.Settings, MR.strings.settings) }, { Text(MR.strings.settings) }),
    LogScreen({ Icon(Icons.Filled.Code, MR.strings.log) }, { Text(MR.strings.log) })
}

@Composable
fun TopAppBar(viewModel: HomeScreenViewModel, snackbarHostState: SnackbarHostState, navController: NavHostController) {
    SmallTopAppBar(
        modifier = Modifier.padding(end = 16.dp),
        title = { Text(MR.strings.appName) },
        actions = {

            val navBackStackEntry by navController.currentBackStackEntryAsState()

            when (navBackStackEntry?.destination?.route) {
                Screens.HomeScreen.name,
                Screens.ConfigurationScreen.name -> HomeAndConfigScreenActions(viewModel, snackbarHostState)
                Screens.SettingsScreen.name -> {}
                Screens.LogScreen.name -> LogScreenActions(viewModel)
            }
        }
    )
}


@Composable
fun HomeAndConfigScreenActions(viewModel: HomeScreenViewModel, snackbarHostState: SnackbarHostState) {
    Row(modifier = Modifier.padding(start = 8.dp)) {
        MicrophonePermissionRequired(viewModel, snackbarHostState)
        OverlayPermissionRequired(viewModel)
        UnsavedChanges(viewModel)
    }
}


@Composable
fun MicrophonePermissionRequired(viewModel: HomeScreenViewModel, snackbarHostState: SnackbarHostState) {
    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(50)),
        visible = viewModel.isMicrophonePermissionRequestRequired.observe()
    ) {
        val microphonePermission = requestMicrophonePermission(snackbarHostState, MR.strings.microphonePermissionInfoWakeWord) {}

        IconButton(
            onClick = { microphonePermission.invoke() },
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            )
        )
        {
            Icon(
                imageVector = Icons.Filled.MicOff,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                contentDescription = MR.strings.microphone
            )
        }
    }
}

@Composable
fun OverlayPermissionRequired(viewModel: HomeScreenViewModel) {
    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(50)),
        visible = viewModel.isOverlayPermissionRequestRequired.observe()
    ) {
        val overlayPermission = requestOverlayPermission {}

        IconButton(onClick = { overlayPermission.invoke() }, Modifier.background(MaterialTheme.colorScheme.errorContainer))
        {
            Icon(
                imageVector = Icons.Filled.LayersClear,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                contentDescription = MR.strings.overlay
            )
        }
    }
}

@Composable
fun UnsavedChanges(viewModel: HomeScreenViewModel) {
    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(0)),
        visible = GlobalData.unsavedChanges.observe()
    ) {
        Row(modifier = Modifier.padding(start = 8.dp)) {
            IconButton(onClick = { viewModel.resetChanges() })
            {
                Icon(
                    imageVector = Icons.Filled.Restore,
                    contentDescription = MR.strings.reset
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { viewModel.saveAndApplyChanges() }) {
                Icon(
                    imageVector = Icons.Filled.PublishedWithChanges,
                    contentDescription = MR.strings.save
                )
            }
        }
    }
    AnimatedVisibility(
        enter = fadeIn(animationSpec = tween(50)),
        exit = fadeOut(animationSpec = tween(50)),
        visible = ServiceInterface.isRestarting.observe()
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val angle by infiniteTransition.animateFloat(
            initialValue = 0F,
            targetValue = 360F,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing)
            )
        )

        Icon(
            modifier = Modifier.rotate(angle),
            imageVector = Icons.Filled.Autorenew,
            contentDescription = MR.strings.reset
        )
    }
}

@Composable
fun LogScreenActions(viewModel: HomeScreenViewModel) {
    Row(modifier = Modifier.padding(start = 8.dp)) {
        IconButton(onClick = { viewModel.shareLogFile() })
        { Icon(imageVector = Icons.Filled.Share, contentDescription = MR.strings.share) }

        IconButton(onClick = { viewModel.saveLogFile() })
        { Icon(imageVector = Icons.Filled.Save, contentDescription = MR.strings.save) }
    }
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