package org.rhasspy.mobile.android.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.screens.LocalMainNavController
import org.rhasspy.mobile.android.screens.MainScreens
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.translate

@Composable
fun AboutSettingsItem() {
    val navController = LocalMainNavController.current
    ListElement(
        modifier = Modifier.clickable {
            navController.navigate(MainScreens.AboutScreen.name)
        },
        icon = { Icon(Icons.Filled.Info, modifier = Modifier.size(24.dp), contentDescription = MR.strings.info) },
        text = { Text(MR.strings.aboutTitle) },
        secondaryText = { Text("${translate(MR.strings.version)} ${BuildKonfig.versionName}") }
    )
}