package org.rhasspy.mobile.android.aboutScreens

import android.content.Context
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.util.withContext
import org.rhasspy.mobile.android.R

// used until https://github.com/mikepenz/AboutLibraries/issues/751 is merged

@Composable
fun LibrariesContainer(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    librariesBlock: (Context) -> Libs = { context ->
        Libs.Builder().withContext(context).build()
    },
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    itemContentPadding: PaddingValues = LibraryDefaults.ContentPadding,
    onLibraryClick: ((Library) -> Unit)? = null,
) {
    val libraries = remember { mutableStateOf<Libs?>(null) }

    val context = LocalContext.current
    LaunchedEffect(libraries) {
        libraries.value = librariesBlock.invoke(context)
    }

    val libs = libraries.value?.libraries
    if (libs != null) {
        Libraries(
            libraries = libs,
            modifier,
            lazyListState,
            contentPadding,
            showAuthor,
            showVersion,
            showLicenseBadges,
            colors,
            itemContentPadding,
            onLibraryClick
        )
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
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    itemContentPadding: PaddingValues = LibraryDefaults.ContentPadding,
    onLibraryClick: ((Library) -> Unit)? = null,
) {
    LazyColumn(modifier, state = lazyListState, contentPadding = contentPadding) {
        items(libraries) { library ->
            val openDialog = rememberSaveable { mutableStateOf(false) }

            Library(library, showAuthor, showVersion, showLicenseBadges, colors, itemContentPadding) {
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
                            Text(stringResource(id = R.string.aboutlibs_ok))
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier.verticalScroll(scrollState),
                        ) {
                            HtmlText(
                                html = library.licenses.firstOrNull()?.htmlReadyLicenseContent.orEmpty(),
                                color = colors.contentColor
                            )
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                )
            }
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