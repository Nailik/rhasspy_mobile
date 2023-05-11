package org.rhasspy.mobile.widget.microphone

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel
import org.rhasspy.mobile.widget.R

class MicrophoneWidget : GlanceAppWidget(), KoinComponent {

    override val sizeMode: SizeMode = SizeMode.Exact

    val viewModel = get<MicrophoneFabViewModel>()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val viewState by viewModel.viewState.collectAsState()

            //used to mimic border, drawable necessary for rounded corner on older devices
            Box(
                modifier = GlanceModifier
                    .appWidgetBackground()
                    .fillMaxSize()
                    .background(
                        ImageProvider(
                            if (viewState.isShowBorder) {
                                R.drawable.microphone_widget_background_error
                            } else getContainerForMicrophoneFabLegacy(
                                isActionEnabled = viewState.isUserActionEnabled,
                                isRecording = viewState.isRecording
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                val size = LocalSize.current

                //inner content
                Box(
                    modifier = GlanceModifier
                        .size(size.width - 8.dp, size.height - 8.dp)
                        .background(
                            ImageProvider(
                                getContainerForMicrophoneFabLegacy(
                                    isActionEnabled = viewState.isUserActionEnabled,
                                    isRecording = viewState.isRecording
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    //microphone icon
                    Image(
                        modifier = GlanceModifier
                            .size(48.dp),
                        provider = ImageProvider(
                            getMicrophoneFabIconLegacy(
                                isMicOn = viewState.isShowMicOn,
                                isActionEnabled = viewState.isUserActionEnabled,
                                isRecording = viewState.isRecording
                            )
                        ),
                        contentDescription = "translate(resource = MR.strings.microphone)",
                        contentScale = ContentScale.FillBounds
                    )
                }

                //box for clickable, else image is not clickable
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .clickable(onClick = actionRunCallback<MicrophoneWidgetAction>()),
                    contentAlignment = Alignment.Center
                ) {}
            }
        }
    }
}

