package org.rhasspy.mobile.android.aboutScreens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.theme.getIsDarkTheme
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.translate

//git commits for changelog https://lowcarbrob.medium.com/android-pro-tip-generating-your-apps-changelog-from-git-inside-build-gradle-19a07533eec4
@Composable
fun AboutScreen() {

    Surface {

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {

            Header()

            LibrariesContainer()

        }
    }

}

@Composable
fun Header() {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 8.dp)
    ) {
        val context = LocalContext.current

        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, 0)
        val versionName = info.versionName
        val versionCode = PackageInfoCompat.getLongVersionCode(info).toInt()
        rememberSystemUiController().setStatusBarColor(MaterialTheme.colorScheme.surfaceVariant, darkIcons = !getIsDarkTheme())
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                .padding(8.dp)
        ) {
            Icon(MR.images.ic_launcher, MR.strings.icon, modifier = Modifier.size(96.dp))
        }

        Text(MR.strings.appName, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(8.dp))

        Text(
            "${translate(MR.strings.version)} $versionName - $versionCode",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )

        AppInformationChips()

    }
}

@Composable
fun AppInformationChips() {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DataPrivacyDialogueButton()
        OutlinedButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Nailik/rhasspy_mobile"))) }) {
            Text("Sourcecode")
        }
        ChangelogDialogueButton()
    }

}
