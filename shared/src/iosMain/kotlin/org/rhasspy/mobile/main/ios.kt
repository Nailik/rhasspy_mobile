package org.rhasspy.mobile.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import co.touchlab.kermit.crashlytics.setCrashlyticsUnhandledExceptionHook
import platform.UIKit.UIViewController

fun MainViewController() : UIViewController =
    Application("Rhasspy Mobile") {
        MaterialTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                FilledTonalButton(modifier = Modifier.align(Alignment.Center), onClick = {}) {
                    Text(text = "Hello Compose Ui! - Material3")
                }
            }
        }
    }

@OptIn(ExperimentalKermitApi::class)
fun setupKermit() {
    Logger.addLogWriter(CrashlyticsLogWriter())
    setCrashlyticsUnhandledExceptionHook()
}