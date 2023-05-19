package org.rhasspy.mobile.ui.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.content.elements.Dialog
import org.rhasspy.mobile.ui.content.elements.HtmlText
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.testTag

/**
 * button to open changelog dialog
 */
@Composable
fun DataPrivacyDialogButton(dataPrivacy: String) {

    var openDialog by rememberSaveable { mutableStateOf(false) }

    OutlinedButton(
        onClick = { openDialog = true },
        modifier = Modifier.testTag(TestTag.DialogDataPrivacyButton)
    ) {
        Text(MR.strings.dataPrivacy.stable)
    }

    if (openDialog) {
        DataPrivacyDialog(
            dataPrivacy = dataPrivacy,
            onDismissRequest = { openDialog = false }
        )
    }

}

/**
 * Dialog to show data privacy information
 */
@Composable
private fun DataPrivacyDialog(
    dataPrivacy: String,
    onDismissRequest: () -> Unit
) {

    val scrollState = rememberScrollState()

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
            Text(MR.strings.dataPrivacy.stable)
        },
        supportingText = {
            Column(
                modifier = Modifier
                    .testTag(TestTag.DialogDataPrivacy)
                    .verticalScroll(scrollState),
            ) {
                HtmlText(
                    html = dataPrivacy,
                    color = LocalContentColor.current
                )
            }
        }
    )

}