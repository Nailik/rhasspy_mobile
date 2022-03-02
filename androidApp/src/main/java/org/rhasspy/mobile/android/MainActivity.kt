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
import androidx.core.view.WindowCompat
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        this.setContent {
            MainScreen()
        }
    }
}

enum class Screens(val icon: @Composable () -> Unit, val label: StringResource) {
    HomeScreen({ Icon(Icons.Filled.Mic, "Localized description") }, MR.strings.home),
    ConfigurationScreen({ Icon(painterResource(R.drawable.ic_launcher), "Localized description", Modifier.size(Dp(24f))) }, MR.strings.configuration),
    SettingsScreen({ Icon(Icons.Filled.Settings, "Localized description") }, MR.strings.settings),
    LogScreen({ Icon(Icons.Filled.Code, "Localized description") }, MR.strings.log)
}