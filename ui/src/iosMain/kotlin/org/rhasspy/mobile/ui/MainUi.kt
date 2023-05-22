package org.rhasspy.mobile.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.icons.RhasspyLogo
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.main.MainScreen
import org.rhasspy.mobile.viewmodel.ViewModelFactory

@Composable
fun MainUi(
    viewModelFactory: ViewModelFactory,
    isHasStarted: StateFlow<Boolean>
) {

    MaterialTheme {

        Crossfade(targetState = isHasStarted.collectAsState().value) { hasStarted ->
            when (hasStarted) {
                true -> MainScreen(viewModelFactory)
                false -> SplashScreen()
            }
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