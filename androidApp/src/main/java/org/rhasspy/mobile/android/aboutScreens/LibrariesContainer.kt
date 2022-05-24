package org.rhasspy.mobile.android.aboutScreens

import android.content.Context
import android.widget.TextView
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.util.withContext
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.CustomDivider
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.android.utils.Text

@Composable
fun LibrariesContainer(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    librariesBlock: (Context) -> Libs = { context ->
        Libs.Builder().withContext(context).build()
    },
    header: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Unit)? = null,
) {
    val libraries = remember { mutableStateOf<Libs?>(null) }

    val context = LocalContext.current
    LaunchedEffect(libraries) {
        libraries.value = librariesBlock.invoke(context)
    }

    libraries.value?.libraries?.also { libs ->
        Libraries(
            libraries = libs,
            modifier,
            lazyListState,
            contentPadding,
            header,
            onLibraryClick
        )
    }
}

/**
 * fix wrong library names
 */
fun Library.getCorrectedName(): String {
    return when {
        uniqueId == "org.fusesource.jansi:jansi" -> "Jansi"
        name == "androidx.customview:poolingcontainer" -> "Poolingcontainer"
        name == "androidx.profileinstaller:profileinstaller" -> "Profileinstaller"
        else -> name
    }
}

/**
 * Displays all provided libraries in a simple list.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Libraries(
    libraries: List<Library>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    header: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Unit)? = null,
) {
    LazyColumn(modifier, state = lazyListState, contentPadding = contentPadding) {
        if (header != null) {
            header()
        }

        items(libraries) { library ->
            val openDialog = rememberSaveable { mutableStateOf(false) }

            Library(library) {
                if (onLibraryClick != null) {
                    onLibraryClick.invoke(library)
                } else {
                    openDialog.value = true
                }
            }

            if (openDialog.value) {
                val scrollState = rememberScrollState()
                AlertDialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    confirmButton = {
                        TextButton(onClick = { openDialog.value = false }) {
                            Text(MR.strings.ok)
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier.verticalScroll(scrollState),
                        ) {
                            HtmlText(
                                html = library.licenses.firstOrNull()?.htmlReadyLicenseContent.orEmpty(),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                )
            }

            CustomDivider()
        }
    }
}

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier, color: Color) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context).apply { setTextColor(color.toArgb()) } },
        update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) }
    )
}


@Composable
internal fun Library(
    library: Library,
    onClick: () -> Unit,
) {
    ListElement(
        modifier = Modifier
            .clickable { onClick.invoke() },
        text = { Text(text = library.getCorrectedName()) },
        secondaryText = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(text = library.author, modifier = Modifier.padding(vertical = 8.dp))
                library.licenses.forEach {
                    Badge(
                        contentColor = MaterialTheme.colorScheme.primaryContainer,
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(modifier = Modifier.padding(4.dp), text = it.name)
                    }
                }
            }
        },
        trailing = { Text(text = library.artifactVersion ?: "") }
    )
}

val Library.author: String
    get() = developers.takeIf { it.isNotEmpty() }?.map { it.name }?.joinToString(", ") ?: organization?.name ?: ""

val License.htmlReadyLicenseContent: String?
    get() = licenseContent?.replace("\n", "<br />")