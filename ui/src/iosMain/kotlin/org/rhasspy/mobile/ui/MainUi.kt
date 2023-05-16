package org.rhasspy.mobile.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.icons.RhasspyLogo
import org.rhasspy.mobile.platformspecific.utils.isDebug
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
                true -> StartScreen(viewModelFactory)
                false -> SplashScreen()
            }
        }

    }

}

@Composable
private fun StartScreen(viewModelFactory: ViewModelFactory) {
    Box(modifier = Modifier.fillMaxSize()) {
        MainScreen(viewModelFactory)
        if (isDebug()) {
            Text(
                text = "DEBUG",
                modifier = Modifier
                    .rotate(45F)
                    .offset(50.dp)
                    .background(Color.Red)
                    .width(150.dp)
                    .align(Alignment.TopEnd),
                textAlign = TextAlign.Center
            )
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