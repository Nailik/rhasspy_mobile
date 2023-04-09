package org.rhasspy.mobile.android.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.content.elements.HtmlText
import org.rhasspy.mobile.android.content.elements.Text
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable

/**
 * button to open changelog dialog
 */
@Composable
fun DataPrivacyDialogButton() {

    var openDialog by rememberSaveable { mutableStateOf(false) }

    OutlinedButton(
        onClick = { openDialog = true },
        modifier = Modifier.testTag(TestTag.DialogDataPrivacyButton)
    ) {
        Text(MR.strings.dataPrivacy.stable)
    }

    if (openDialog) {
        DataPrivacyDialog {
            openDialog = false
        }
    }

}

/**
 * Dialog to show data privacy information
 */
@Composable
private fun DataPrivacyDialog(onDismissRequest: () -> Unit) {

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag(TestTag.DialogOk)
            ) {
                Text(MR.strings.close.stable)
            }
        },
        title = {
            Text(MR.strings.dataPrivacy.stable)
        },
        text = {
            Column(
                modifier = Modifier
                    .testTag(TestTag.DialogDataPrivacy)
                    .verticalScroll(scrollState),
            ) {
                HtmlText(
                    html = MR.files.dataprivacy.readText(LocalContext.current),
                    color = LocalContentColor.current
                )
            }
        }
    )

}