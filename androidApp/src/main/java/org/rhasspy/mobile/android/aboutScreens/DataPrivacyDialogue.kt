package org.rhasspy.mobile.android.aboutScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.utils.Text

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DataPrivacyDialogueButton() {
    val openDialog = rememberSaveable { mutableStateOf(false) }

    OutlinedButton(onClick = { openDialog.value = true }) {
        Text(MR.strings.dataPrivacy)
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
                    Text(MR.strings.dataPrivacy)
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }
}