@file:Suppress("unused", "FunctionName")

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            FilledTonalButton(modifier = Modifier.align(Alignment.Center), onClick = {}) {
                Text(text = "Hello Compose Ui! - Material3")
            }
        }
    }
}