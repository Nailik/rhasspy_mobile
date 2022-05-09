package org.rhasspy.mobile.android.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import org.rhasspy.mobile.android.aboutScreens.AboutScreen
import org.rhasspy.mobile.android.bottomBarScreens.MainScreens
import org.rhasspy.mobile.android.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreenNavigation() {

    AppTheme(true) {

        ProvideWindowInsets {

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = MainScreens.BoomBarScreen.name,
                ) {
                    composable(MainScreens.BoomBarScreen.name) {
                        BottomBarScreensNavigation(mainNavController = navController)
                    }
                    composable(MainScreens.AboutScreen.name) {
                        AboutScreen()
                    }
                }
            }
        }
    }
}



