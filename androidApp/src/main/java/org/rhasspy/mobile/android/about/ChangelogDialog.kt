package org.rhasspy.mobile.android.about

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag

/**
 * button to open changelog dialog
 */
@Composable
fun ChangelogDialogButton(changelog: ImmutableList<String>) {

    var openDialog by rememberSaveable { mutableStateOf(false) }

    OutlinedButton(
        onClick = { openDialog = true },
        modifier = Modifier.testTag(TestTag.DialogChangelogButton)
    ) {
        Text(MR.strings.changelog.stable)
    }

    if (openDialog) {
        ChangelogDialog(
            changelog = changelog,
            onDismissRequest = { openDialog = false }
        )
    }

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
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.close.stable)
            }
        },
        headline = {
            Text(MR.strings.changelog.stable)
        },
        supportingText = {
            LazyColumn(
                modifier = Modifier.testTag(TestTag.DialogChangelog)
            ) {
                items(changelog) { item ->
                    Text(text = item)
                }
            }
        }
    )

}