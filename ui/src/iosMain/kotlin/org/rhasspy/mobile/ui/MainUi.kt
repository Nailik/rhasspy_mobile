package org.rhasspy.mobile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Application
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.elements.LocalImage

@Composable
fun MainUi(isHasStarted: StateFlow<Boolean>) { //TODO splash view
    MaterialTheme {
        if (isHasStarted.collectAsState().value) {
            Box(modifier = Modifier.fillMaxSize()) {
                FilledTonalButton(modifier = Modifier.align(Alignment.Center), onClick = {}) {
                    Text(text = "Hello Compose Ui! - Material3")
                    Text(text = StringDesc.Resource(MR.strings.backgroundServiceInformation).localized())
                }

            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {/*
                Image(
                    painter = painterResource(MR.images.ic_launcher),
                    contentDescription = "text",
                  //  modifier = modifier,
                 //   alignment = alignment,
                    contentScale = ContentScale.FillWidth,
                   // alpha = alpha
                )
                LocalImage(
                    imageResource = MR.images.ic_launcher,
                    isGreyscale = true,
                    contentDescription = "text",
                    contentScale = ContentScale.FillWidth
                )*/
            }
        }
    }
}