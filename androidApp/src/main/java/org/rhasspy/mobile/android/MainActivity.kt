package org.rhasspy.mobile.android

import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import org.rhasspy.mobile.Greeting

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        this.setContent {
            StartScreen()
        }
    }
}

@Preview
@Composable
private fun HomeScreen() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        // Main Screen
        composable("main_screen") {
            StartScreen()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen() {
    val navController = rememberNavController()
    Scaffold(topBar = {
        MediumTopAppBar(
            title = { Text(text = "hello2") }
        )
    },
        //https://developer.android.com/jetpack/compose/navigation#:~:text=correctly%20saved%20and%20restored%20as%20you%20swap%20between%20bottom%20navigation%20items.
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                NavigationBarItem(selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,  onClick = {
                    navController.navigate("home") {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }, icon = {
                    Icon(
                        Icons.Filled.Mic, contentDescription = "Localized " +
                                "description"
                    )
                }, label = { Text("Home") })
                NavigationBarItem(selected = currentDestination?.hierarchy?.any { it.route == "eins" } == true, onClick = {
                    navController.navigate("eins" ) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }, icon = {
                    Icon(
                        Icons.Filled.Build, contentDescription = "Localized " +
                                "description"
                    )
                })
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "zwei" } == true,
                    onClick = {
                        navController.navigate("zwei") {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Build, contentDescription = "Localized description") })
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "home") {
            composable("home") { Text("home") }
            composable("eins") { Text("eins") }
            composable("zwei") { Text("zwei") }
        }
    }
}


//by https://www.geeksforgeeks.org/animated-splash-screen-in-android-using-jetpack-compose/
@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(3000L)
        navController.navigate("main_screen")
    }

    // Image
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}