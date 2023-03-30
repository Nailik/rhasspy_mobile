package org.rhasspy.mobile.android.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.layout.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.R
import org.rhasspy.mobile.android.main.getContainerForMicrophoneFabLegacy
import org.rhasspy.mobile.android.main.getMicrophoneFabIconLegacy
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel

class MicrophoneWidget : GlanceAppWidget(), KoinComponent {

    override val sizeMode: SizeMode = SizeMode.Exact

    @Composable
    @GlanceComposable
    @Suppress("StateFlowValueCalledInComposition") //suppress because it doesn't work in glance
    override fun Content() {
        val viewModel = get<MicrophoneFabViewModel>()

        //used to mimic border, drawable necessary for rounded corner on older devices
        Box(
            modifier = GlanceModifier
                .appWidgetBackground()
                .fillMaxSize()
                .background(
                    ImageProvider(
                        if (viewModel.isShowBorder.value) {
                            R.drawable.microphone_widget_background_error
                        } else getContainerForMicrophoneFabLegacy(
                            viewModel.isUserActionEnabled.value,
                            viewModel.isRecording.value
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
                                viewModel.isUserActionEnabled.value,
                                viewModel.isRecording.value
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
                            viewModel.isShowMicOn.value,
                            viewModel.isUserActionEnabled.value,
                            viewModel.isRecording.value
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

