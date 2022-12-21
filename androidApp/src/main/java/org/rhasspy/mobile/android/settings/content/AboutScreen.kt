package org.rhasspy.mobile.android.settings.content

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
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
import org.rhasspy.mobile.android.settings.SettingsScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.viewModels.AboutScreenViewModel

/**
 * About Screen contains A Header with Information,
 * and list of used dependencies
 */
@Composable
fun AboutScreen(viewModel: AboutScreenViewModel = get()) {
    Surface(modifier = Modifier.testTag(SettingsScreens.AboutSettings)) {
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

        AppInformationChips(viewModel.changelogText, viewModel::onOpenSourceCode)
    }
}

/**
 * image of app icon and back press
 */
@Composable
fun AppIcon() {
    Box(modifier = Modifier.fillMaxWidth()) {

        val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        IconButton(
            onClick = { onBackPressedDispatcher?.onBackPressed() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .testTag(TestTag.AppBarBackButton)
        ) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = MR.strings.close)
        }

        Icon(
            imageResource = MR.images.ic_launcher,
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
fun AppInformationChips(changelogText: String, onOpenSourceCode: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DataPrivacyDialogButton()
        OutlinedButton(onClick = onOpenSourceCode) {
            Icon(Icons.Filled.Link, MR.strings.sourceCode)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(MR.strings.sourceCode)
        }
        ChangelogDialogButton(changelogText)
    }
}