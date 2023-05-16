package org.rhasspy.mobile.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.icons.RhasspyLogo
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.elements.Icon

@Composable
fun MainUi(isHasStarted: StateFlow<Boolean>) {

    MaterialTheme {

        Crossfade(targetState = isHasStarted.collectAsState().value) { hasStarted ->
            when (hasStarted) {
                true -> StartScreen()
                false -> SplashScreen()
            }
        }

    }

}

@Composable
private fun StartScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        FilledTonalButton(modifier = Modifier.align(Alignment.Center), onClick = {}) {
            Text(text = "Hello Compose Ui! - Material3")
            Text(text = StringDesc.Resource(MR.strings.backgroundServiceInformation).localized())
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            RhasspyLogo,
            MR.strings.appName.stable,
            Modifier.fillMaxSize().padding(24.dp)
        )
    }
}