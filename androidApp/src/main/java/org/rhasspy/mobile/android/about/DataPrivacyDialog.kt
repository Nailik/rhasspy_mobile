package org.rhasspy.mobile.android.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.Text

/**
 * button to open changelog dialog
 */
@Composable
fun DataPrivacyDialogButton() {
    var openDialog by rememberSaveable { mutableStateOf(false) }

    OutlinedButton(onClick = { openDialog = true }) {
        Text(MR.strings.dataPrivacy)
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
fun DataPrivacyDialog(onDismissRequest: () -> Unit) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(MR.strings.close)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(scrollState),
            ) {
                Text(MR.strings.dataPrivacy)
            }
        }
    )
}