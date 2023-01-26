package org.rhasspy.mobile.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Application
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import co.touchlab.kermit.crashlytics.setCrashlyticsUnhandledExceptionHook
import platform.UIKit.UIViewController

fun MainViewController() : UIViewController =
    Application("Rhasspy Mobile") {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(modifier = Modifier.align(Alignment.Center), text = "Hello Compose Ui!")
        }
    }

@OptIn(ExperimentalKermitApi::class)
fun setupKermit() {
    Logger.addLogWriter(CrashlyticsLogWriter())
    setCrashlyticsUnhandledExceptionHook()
}