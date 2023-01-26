package org.rhasspy.mobile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Application
import platform.UIKit.UIViewController

class IosApplication() : Application() {

    init {
        onCreated()
    }

    @Suppress("unused", "FunctionName")
    fun MainViewController(): UIViewController =
        Application("Rhasspy Mobile") { //TODO splash view
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    FilledTonalButton(modifier = Modifier.align(Alignment.Center), onClick = {}) {
                        Text(text = "Hello Compose Ui! - Material3")
                    }
                }
            }
        }

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        //TODO call ios
    }

    override fun startOverlay() {
        //TODO??
    }

    override fun stopOverlay() {
        //TODO??
    }

    override suspend fun updateWidget() {
        //TODO??
    }

}