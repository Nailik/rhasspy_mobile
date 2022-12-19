package org.rhasspy.mobile.android.widget

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.unit.ColorProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.R
import org.rhasspy.mobile.android.theme.DarkThemeColors
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

//TODO improve design for older devices
//TODO check rounded cornder
//TODO check snackbar
class MicrophoneWidget : GlanceAppWidget() {

    @Composable
    @GlanceComposable
    override fun Content() {
        MaterialTheme(DarkThemeColors) {
            Column(
                modifier = GlanceModifier
                    .appWidgetBackground()
                    .fillMaxSize()
                    .background(ImageProvider(R.drawable.microphone_widget_background))
            ) {
                Button(
                    text = "text",
                    onClick = actionRunCallback<TestCallback>(),
                    colors = ButtonColors(
                        ColorProvider(MaterialTheme.colorScheme.error),
                        ColorProvider(MaterialTheme.colorScheme.errorContainer)
                    ),
                    modifier = GlanceModifier
                        .fillMaxSize()
                    // .background(ImageProvider(R.drawable.microphone_widget_background))
                )
            }
        }
    }
}


class TestCallback : ActionCallback, KoinComponent {


    private val viewModel = get<HomeScreenViewModel>()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        viewModel.toggleSession()
    }
}
