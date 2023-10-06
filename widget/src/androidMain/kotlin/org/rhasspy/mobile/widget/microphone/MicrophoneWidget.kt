package org.rhasspy.mobile.widget.microphone

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewModel
import org.rhasspy.mobile.widget.R

class MicrophoneWidget : GlanceAppWidget(), KoinComponent {

    override val sizeMode: SizeMode = SizeMode.Exact

    val viewModel = get<MicrophoneFabViewModel>()

    private var updateJob: Job? = null

    private fun initUpdateJob() {
        if (updateJob != null) return

        updateJob = CoroutineScope(Dispatchers.IO).launch {
            viewModel.viewState.collect {
                //necessary to keep widget up to date
                MicrophoneWidgetUtils.updateWidget()
            }
        }
    }

    @Suppress("StateFlowValueCalledInComposition") //suppress because it doesn't work in glance
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        initUpdateJob()

        provideContent {
            //collect as state is not working
            val viewState = viewModel.viewState.value

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
                                isMicOn = viewState.isMicrophonePermissionAllowed,
                                isRecording = viewState.isRecording
                            )
                        ),
                        contentDescription = MR.strings.microphone.getString(get<NativeApplication>()),
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

