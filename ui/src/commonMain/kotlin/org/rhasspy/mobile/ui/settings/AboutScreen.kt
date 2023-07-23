package androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.data.libraries.StableLibrary
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.resources.icons.RhasspyLogo
import org.rhasspy.mobile.ui.LocalSnackBarHostState
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.Screen
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.CustomDivider
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.HtmlText
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.AboutSettings
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action.OpenSourceCode
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Change.CloseChangelog
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Change.CloseDataPrivacy
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Change.CloseLibrary
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Change.OpenChangelog
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Change.OpenDataPrivacy
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Change.OpenLibrary
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewState

/**
 * About Screen contains A Header with Information,
 * and list of used dependencies
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AboutScreen() {

    val viewModel: AboutScreenViewModel = LocalViewModelFactory.current.getViewModel()

    Screen(screenViewModel = viewModel) {
        val viewState by viewModel.viewState.collectAsState()
        val snackBarHostState = LocalSnackBarHostState.current
        val snackBarText = viewState.snackBarText?.let { translate(it) }

        LaunchedEffect(snackBarText) {
            snackBarText?.also {
                snackBarHostState.showSnackbar(message = it)
                viewModel.onEvent(ShowSnackBar)
            }
        }


        Surface(modifier = Modifier.testTag(AboutSettings)) {

            if (viewState.isLibraryDialogVisible) {
                viewState.libraryDialogContent?.also {
                    LibraryDialog(
                        stableLibrary = it,
                        onDismissRequest = { viewModel.onEvent(CloseLibrary) })
                }
            }

            BoxWithConstraints {

                LibrariesContainer(
                    libraries = viewState.libraries,
                    header = {
                        if (maxHeight > 600.dp) {
                            stickyHeader {
                                Header(viewModel)
                            }
                        } else {
                            item {
                                Header(viewModel)
                            }
                        }
                    },
                    onLibraryClick = { viewModel.onEvent(OpenLibrary(it)) }
                )
            }
        }
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

    if (viewState.isPrivacyDialogVisible) {
        DataPrivacyDialog(
            dataPrivacy = viewState.privacy,
            onDismissRequest = { onEvent(CloseDataPrivacy) }
        )
    }

    if (viewState.isChangelogDialogVisible) {
        ChangelogDialog(
            changelog = viewState.changelog,
            onDismissRequest = { onEvent(CloseChangelog) }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = { onEvent(OpenDataPrivacy) },
            modifier = Modifier.testTag(TestTag.DialogDataPrivacyButton)
        ) {
            Text(MR.strings.dataPrivacy.stable)
        }

        OutlinedButton(onClick = { onEvent(OpenSourceCode) }) {
            Text(MR.strings.sourceCode.stable)
        }

        OutlinedButton(
            onClick = { onEvent(OpenChangelog) },
            modifier = Modifier.testTag(TestTag.DialogChangelogButton)
        ) {
            Text(MR.strings.changelog.stable)
        }
    }
}


/**
 * Dialog to show data privacy information
 */
@Composable
fun DataPrivacyDialog(
    dataPrivacy: String,
    onDismissRequest: () -> Unit
) {

    val scrollState = rememberScrollState()

    Dialog(
        testTag = TestTag.DialogDataPrivacy,
        title = MR.strings.dataPrivacy.stable,
        supportingText = {
            Column(
                modifier = Modifier.verticalScroll(scrollState),
            ) {
                HtmlText(
                    html = dataPrivacy,
                    color = LocalContentColor.current
                )
            }
        },
        confirmLabel = MR.strings.close.stable,
        onConfirm = onDismissRequest,
        onDismiss = onDismissRequest
    )

}

/**
 * Displays changelog as text in a dialog
 */
@Composable
private fun ChangelogDialog(
    changelog: ImmutableList<String>,
    onDismissRequest: () -> Unit
) {

    Dialog(
        testTag = TestTag.DialogChangelog,
        title = MR.strings.changelog.stable,
        supportingText = {
            LazyColumn {
                items(changelog) { item ->
                    Text(text = item)
                }
            }
        },
        confirmLabel = MR.strings.close.stable,
        onConfirm = onDismissRequest,
        onDismiss = onDismissRequest
    )

}


/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun LibrariesContainer(
    libraries: ImmutableList<StableLibrary>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    header: LazyListScope.() -> Unit,
    onLibraryClick: (StableLibrary) -> Unit,
) {
    LazyColumn(
        modifier = modifier.testTag(TestTag.LibrariesContainer),
        state = lazyListState,
        contentPadding = contentPadding
    ) {
        header()

        items(libraries) { library ->
            Library(
                stableLibrary = library,
                onClick = { onLibraryClick(library) }
            )

            CustomDivider()
        }
    }

}

/**
 * Library list element
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Library(
    stableLibrary: StableLibrary,
    onClick: () -> Unit,
) {

    ListElement(
        modifier = Modifier.clickable { onClick.invoke() },
        text = {
            Text(stableLibrary.library.name)
        },
        secondaryText = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = stableLibrary.library.author,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                stableLibrary.library.licenses.forEach {
                    Badge(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = it.name
                        )
                    }
                }
            }
        },
        trailing = {
            Text(stableLibrary.library.artifactVersion ?: "")
        }
    )

}


/**
 * Library dialog with more information
 */
@Composable
fun LibraryDialog(
    stableLibrary: StableLibrary,
    onDismissRequest: () -> Unit
) {

    Dialog(
        testTag = TestTag.DialogLibrary,
        supportingText = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                HtmlText(
                    html = stableLibrary.library.licenses.firstOrNull()?.htmlReadyLicenseContent.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmLabel = MR.strings.ok.stable,
        onConfirm = onDismissRequest,
        onDismiss = onDismissRequest
    )

}

/**
 * get author of library
 */
private val Library.author: String
    get() = developers.takeIf { it.isNotEmpty() }?.map { it.name }?.joinToString(", ")
        ?: organization?.name ?: ""

/**
 * read html license content
 */
private val License.htmlReadyLicenseContent: String?
    get() = licenseContent?.replace("\n", "<br />")