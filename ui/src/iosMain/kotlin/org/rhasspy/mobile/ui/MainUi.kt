package org.rhasspy.mobile.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.icons.RhasspyLogo
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.about.ChangelogDialogButton
import org.rhasspy.mobile.ui.about.DataPrivacyDialogButton
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
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DataPrivacyDialogButton("iewState.privacy")
        OutlinedButton(onClick = { }) {
            org.rhasspy.mobile.ui.content.elements.Text(MR.strings.sourceCode.stable)
        }
        ChangelogDialogButton(persistentListOf("viewState.changelog"))
    }
    /*
    Box(modifier = Modifier.fillMaxSize()) {
        FilledTonalButton(modifier = Modifier.align(Alignment.Center), onClick = {}) {
            Text(text = "Hello Compose Ui! - Material3")
            Text(text = StringDesc.Resource(MR.strings.backgroundServiceInformation).localized())
        }
    }*/
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