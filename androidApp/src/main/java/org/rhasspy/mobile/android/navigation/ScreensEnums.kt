package org.rhasspy.mobile.android.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text

enum class BottomBarScreens(val icon: @Composable () -> Unit, val label: @Composable () -> Unit) {
    HomeScreen({ Icon(Icons.Filled.Mic, MR.strings.home) }, { Text(MR.strings.home) }),
    ConfigurationScreen(
        { Icon(painterResource(MR.images.ic_launcher.drawableResId), MR.strings.configuration, Modifier.size(24.dp)) },
        { Text(MR.strings.configuration) }),
    SettingsScreen({ Icon(Icons.Filled.Settings, MR.strings.settings) }, { Text(MR.strings.settings) }),
    LogScreen({ Icon(Icons.Filled.Code, MR.strings.log) }, { Text(MR.strings.log) })
}

enum class MainScreens {
    BoomBarScreen,
    AboutScreen
}