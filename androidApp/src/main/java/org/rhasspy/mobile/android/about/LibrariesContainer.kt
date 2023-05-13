package org.rhasspy.mobile.android.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.android.content.elements.CustomDivider
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.data.libraries.StableLibrary
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.HtmlText
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag

/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun LibrariesContainer(
    libraries: ImmutableList<StableLibrary>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    header: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((StableLibrary) -> Unit)? = null,
) {

    LazyColumn(
        modifier = modifier.testTag(TestTag.LibrariesContainer),
        state = lazyListState,
        contentPadding = contentPadding
    ) {
        if (header != null) {
            header()
        }

        items(libraries) { library ->
            var openDialog by rememberSaveable { mutableStateOf(false) }

            Library(library) {
                if (onLibraryClick != null) {
                    onLibraryClick.invoke(library)
                } else {
                    openDialog = true
                }
            }

            if (openDialog) {
                LibraryDialog(library) {
                    openDialog = false
                }
            }

            CustomDivider()
        }
    }

}

/**
 * Library list element
 */
@Composable
private fun Library(
    stableLibrary: StableLibrary,
    onClick: () -> Unit,
) {

    ListElement(
        modifier = Modifier.clickable { onClick.invoke() },
        text = {
            Text(stableLibrary.library.correctedName)
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
private fun LibraryDialog(stableLibrary: StableLibrary, onDismissRequest: () -> Unit) {

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.ok.stable)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .testTag(TestTag.DialogLibrary)
                    .verticalScroll(scrollState),
            ) {
                HtmlText(
                    html = stableLibrary.library.licenses.firstOrNull()?.htmlReadyLicenseContent.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )

}


/**
 * fix wrong library names
 */
private val Library.correctedName: String
    get() = when {
        uniqueId == "org.fusesource.jansi:jansi" -> "Jansi"
        name == "androidx.customview:poolingcontainer" -> "Poolingcontainer"
        name == "androidx.profileinstaller:profileinstaller" -> "Profileinstaller"
        else -> name
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