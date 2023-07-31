package androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.data.libraries.StableLibrary
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.resources.icons.RhasspyLogo
import org.rhasspy.mobile.ui.*
import org.rhasspy.mobile.ui.content.elements.*
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.AboutSettings
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action.OpenSourceCode
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Change.*
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
        title = "${translate(MR.strings.changelog.stable)} - ${BuildKonfig.versionName}",
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