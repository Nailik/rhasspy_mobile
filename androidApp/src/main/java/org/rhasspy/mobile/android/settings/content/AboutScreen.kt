package org.rhasspy.mobile.android.settings.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.android.about.ChangelogDialogButton
import org.rhasspy.mobile.android.about.DataPrivacyDialogButton
import org.rhasspy.mobile.android.about.LibrariesContainer
import org.rhasspy.mobile.android.main.LocalSnackbarHostState
import org.rhasspy.mobile.android.main.LocalViewModelFactory
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.icons.RhasspyLogo
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.AboutSettings
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action.OpenSourceCode
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewState

/**
 * About Screen contains A Header with Information,
 * and list of used dependencies
 */
@Composable
fun AboutScreen() {

    val viewModel: AboutScreenViewModel = LocalViewModelFactory.current.getViewModel()
    val viewState by viewModel.viewState.collectAsState()
    val snackBarHostState = LocalSnackbarHostState.current
    val snackBarText = viewState.snackBarText?.let { translate(it) }

    LaunchedEffect(snackBarText) {
        snackBarText?.also {
            snackBarHostState.showSnackbar(message = it)
            viewModel.onEvent(ShowSnackBar)
        }
    }


    Surface(modifier = Modifier.testTag(AboutSettings)) {
        val configuration = LocalConfiguration.current
        LibrariesContainer(
            libraries = viewState.libraries,
            header = {
                if (configuration.screenHeightDp.dp > 600.dp) {
                    stickyHeader {
                        Header(viewModel)
                    }
                } else {
                    item {
                        Header(viewModel)
                    }
                }
            }
        )
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
        AppIcon(viewModel::onEvent)

        Text(
            resource = MR.strings.appName.stable,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = "${translate(MR.strings.version.stable)} ${BuildKonfig.versionName}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )

        val viewState by viewModel.viewState.collectAsState()

        AppInformationChips(
            viewState = viewState,
            onEvent = viewModel::onEvent
        )
    }
}

/**
 * image of app icon and back press
 */
@Composable
fun AppIcon(onEvent: (AboutScreenUiEvent) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {

        IconButton(
            onClick = { onEvent(BackClick) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .testTag(TestTag.AppBarBackButton)
        ) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = MR.strings.close.stable)
        }

        Icon(
            imageVector = RhasspyLogo,
            contentDescription = MR.strings.icon.stable,
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
fun AppInformationChips(
    viewState: AboutScreenViewState,
    onEvent: (AboutScreenUiEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DataPrivacyDialogButton(viewState.privacy)
        OutlinedButton(onClick = { onEvent(OpenSourceCode) }) {
            Text(MR.strings.sourceCode.stable)
        }
        ChangelogDialogButton(viewState.changelog)
    }
}