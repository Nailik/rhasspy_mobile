package org.rhasspy.mobile.android.aboutScreens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.translate
import org.rhasspy.mobile.viewModels.AboutScreenViewModel

/**
 * About Screen contains A Header with Information,
 * and list of used dependencies
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AboutScreen(viewModel: AboutScreenViewModel = viewModel()) {
    Surface {
        val configuration = LocalConfiguration.current
        LibrariesContainer(header = {
            if (configuration.screenHeightDp.dp > 600.dp) {
                stickyHeader {
                    Header(viewModel)
                }
            } else {
                item {
                    Header(viewModel)
                }
            }
        })
    }
}

/**
 * Header with chips to open Information
 * shows app version
 */
@Composable
fun Header(viewModel: AboutScreenViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                .padding(8.dp)
        ) {
            Icon(MR.images.ic_launcher, MR.strings.icon, modifier = Modifier.size(96.dp))
        }

        Text(MR.strings.appName, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(8.dp))

        Text(
            "${translate(MR.strings.version)} ${BuildKonfig.versionName}-${BuildKonfig.versionCode}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )

        AppInformationChips(viewModel.changelogText, viewModel::onOpenSourceCode)
    }
}

/**
 * Chips to show data privacy, link to source code and changelog
 */
@Composable
fun AppInformationChips(changelogText: String, onOpenSourceCode: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DataPrivacyDialogButton()
        OutlinedButton(onClick = onOpenSourceCode) {
            Text(MR.strings.sourceCode)
        }
        ChangelogDialogButton(changelogText)
    }
}