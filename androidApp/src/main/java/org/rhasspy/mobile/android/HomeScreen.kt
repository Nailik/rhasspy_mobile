package org.rhasspy.mobile.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.rhasspy.mobile.MR

@Composable
fun HomeScreen() {

    Column {
        Row(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
                .background(color = Color.Red)
        ) {
            var text by remember { mutableStateOf("") }

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.clearFocusOnKeyboardDismiss(),
                label = { Text(MR.strings.configuration) }
            )
        }

        Row(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
                .background(color = Color.Blue)
        ) {
        }
    }
}