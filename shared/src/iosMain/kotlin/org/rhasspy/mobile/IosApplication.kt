package org.rhasspy.mobile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Application
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.ui.LocalImage
import platform.UIKit.UIViewController

@Suppress("unused")
class IosApplication : Application() {

    init {
        onCreated()
    }

    @Suppress("FunctionName")
    fun MainViewController(): UIViewController =
        Application("Rhasspy Mobile") { //TODO splash view
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    FilledTonalButton(modifier = Modifier.align(Alignment.Center), onClick = {}) {
                        Text(text = "Hello Compose Ui! - Material3")
                        Text(text = StringDesc.Resource(MR.strings.backgroundServiceInformation).localized())
                    }

                }

                LocalImage(
                    imageResource = MR.images.ic_launcher,
                    isGreyscale = true,
                    modifier = Modifier,
                    contentDescription = "text",
                    contentScale = ContentScale.FillWidth
                )
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