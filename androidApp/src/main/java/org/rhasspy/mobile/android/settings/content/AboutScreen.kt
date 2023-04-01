package org.rhasspy.mobile.android.settings.content

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.about.ChangelogDialogButton
import org.rhasspy.mobile.android.about.DataPrivacyDialogButton
import org.rhasspy.mobile.android.about.LibrariesContainer
import org.rhasspy.mobile.android.content.elements.Icon
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.content.elements.translate
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.icons.RhasspyLogo
import org.rhasspy.mobile.viewmodel.screens.AboutScreenViewModel

/**
 * About Screen contains A Header with Information,
 * and list of used dependencies
 */
@Composable
fun AboutScreen(viewModel: AboutScreenViewModel = get()) {
    Surface(modifier = Modifier.testTag(SettingsScreenType.AboutSettings)) {
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
            .padding(bottom = 16.dp)
    ) {
        AppIcon()

        Text(
            resource = MR.strings.appName,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = "${translate(MR.strings.version)} ${BuildKonfig.versionName}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )

        AppInformationChips(viewModel::onOpenSourceCode)
    }
}

/**
 * image of app icon and back press
 */
@Composable
fun AppIcon() {
    Box(modifier = Modifier.fillMaxWidth()) {

        val onBackPressedDispatcher =
            LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        IconButton(
            onClick = { onBackPressedDispatcher?.onBackPressed() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .testTag(TestTag.AppBarBackButton)
        ) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = MR.strings.close)
        }

        Icon(
            imageVector = RhasspyLogo,
            contentDescription = MR.strings.icon,
            modifier = Modifier
                .padding(top = 16.dp)
                .size(96.dp)
                .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                .padding(8.dp)
                .align(Alignment.Center)
        )
    }
}

/**
 * Chips to show data privacy, link to source code and changelog
 */
@Composable
fun AppInformationChips(onOpenSourceCode: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DataPrivacyDialogButton()
        OutlinedButton(onClick = onOpenSourceCode) {
            Text(MR.strings.sourceCode)
        }
        ChangelogDialogButton()
    }
}