package org.rhasspy.mobile.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import org.rhasspy.mobile.MR

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        val splashWasDisplayed = savedInstanceState != null
        if (!splashWasDisplayed) {
            installSplashScreen()
        }

        this.setContent {
            MainScreen()
        }
    }
}

enum class Screens(val icon: @Composable () -> Unit, val label: @Composable () -> Unit) {
    HomeScreen({ Icon(Icons.Filled.Mic, "Localized description") }, { Text(MR.strings.home) }),
    ConfigurationScreen(
        { Icon(painterResource(R.drawable.ic_launcher), "Localized description", Modifier.size(Dp(24f))) },
        { Text(MR.strings.configuration) }),
    SettingsScreen({ Icon(Icons.Filled.Settings, "Localized description") }, { Text(MR.strings.settings) }),
    LogScreen({ Icon(Icons.Filled.Code, "Localized description") }, { Text(MR.strings.log) })
}