package org.rhasspy.mobile.ui.assistant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.overlay.IndicationContent
import org.rhasspy.mobile.ui.theme.AppTheme
import org.rhasspy.mobile.viewmodel.ViewModelFactory


@Composable
fun AssistantScreen(viewModelFactory: ViewModelFactory) {

    AppTheme {
        CompositionLocalProvider(
            LocalViewModelFactory provides viewModelFactory
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                IndicationContent()
            }
        }
    }

}