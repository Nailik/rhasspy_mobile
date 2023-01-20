package org.rhasspy.mobile.android.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.testTag

/**
 * button to open changelog dialog
 */
@Composable
fun ChangelogDialogButton(changelogText: String) {

    var openDialog by rememberSaveable { mutableStateOf(false) }

    OutlinedButton(
        onClick = { openDialog = true },
        modifier = Modifier.testTag(TestTag.DialogChangelogButton)
    ) {
        Text(MR.strings.changelog)
    }

    if (openDialog) {
        ChangelogDialog(changelogText) {
            openDialog = false
        }
    }

}

/**
 * Displays changelog as text in a dialog
 */
@Composable
private fun ChangelogDialog(changelogText: String, onDismissRequest: () -> Unit) {

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.close)
            }
        },
        title = {
            Text(MR.strings.changelog)
        },
        text = {
            Column(
                modifier = Modifier
                    .testTag(TestTag.DialogChangelog)
                    .verticalScroll(scrollState),
            ) {
                changelogText.split("\\\\")
                    .map { it.replace("\n", "") }
                    .filter { it.isNotEmpty() }
                    .forEach {
                        Text(text = "· $it")
                    }
            }
        }
    )

}